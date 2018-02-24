package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboardDataProvider {

    private static final long TIME_PASSED_MILLIS = 10 * 60 * 1000;

    private static DCDashboardDataProvider instance;
    private DCDashboard dashboard;
    private DCJourney journey;

    private final List<DCRoutine> routines = Collections.synchronizedList(new ArrayList<DCRoutine>());
    private ArrayList<IDCDashboardObserver> dashboardObservers;

    private boolean inRequest = false;
    private boolean inRequestJourney = false;
    private Date lastJourneyPopupShownDate;

    public static synchronized DCDashboardDataProvider getInstance() {
        if (instance == null)
            instance = new DCDashboardDataProvider();

        return instance;
    }

    private DCDashboardDataProvider() {
        dashboardObservers = new ArrayList<>();
        loadDashboard();
        loadRoutines();
        sync(true);
    }

    /**
     * Add observer to observe DCDashboardDataProvider changes
     * @param observer  IDCDashboardObserver
     */
    public void addObserver(IDCDashboardObserver observer) {
        if (!dashboardObservers.contains(observer)) {
            dashboardObservers.add(observer);
        }
    }

    /**
     * Remove the given observer from observers
     * @param observer
     */
    public void removeObserver(IDCDashboardObserver observer) {
        dashboardObservers.remove(observer);
    }

    public void onJourneyPopupShown() {
        this.lastJourneyPopupShownDate = new Date();
    }

    public boolean shouldShowPopup() {
        if (lastJourneyPopupShownDate == null)
            return true;

        Date now = new Date();
        return now.getTime() - lastJourneyPopupShownDate.getTime() > TIME_PASSED_MILLIS;
    }

    /**
     * Initiates a dashboard update from either user saved data or makes an api call
     * and notify all observers
     *
     * @param hard  explicitly make an api call to refresh the dashboard object
     */
    public void updateDashboard(boolean hard) {
        loadDashboard();
        if (!hard && dashboard != null) {
            notifyObserversOnDashboardUpdated();
        } else {
            if (inRequest)
                return;

            inRequest = true;
            DCApiManager.getInstance().getDashboard(new DCResponseListener<DCDashboard>() {
                @Override
                public void onFailure(DCError error) {
                    notifyObserversOnDashboardUpdated();
                    notifyObserversOnError(error);
                    inRequest = false;
                }

                @Override
                public void onResponse(DCDashboard object) {
                    if (object != null) {
                        dashboard = object;
                        saveDashboard();
                    }
                    notifyObserversOnDashboardUpdated();
                    inRequest = false;

                    synchronized (routines) {
                        if (routines.size() > 0)
                            notifyObserversOnSyncNeeded(routines.toArray(new DCRoutine[routines.size()]));
                    }
                }
            });
        }
    }

    /**
     * Adds the routine to the stack and tries to sync it with the backend
     * On successful api call the dashboard is updated
     *
     * @param routine    Valid DCRoutine
     */
    public void addRoutine(final DCRoutine routine, final DCResponseListener<DCRoutine> listener) {
        if (routine == null || !routine.isValid()) {
            notifyObserversOnError(new DCError(R.string.dashboard_too_short_record));
            return;
        }

        if (routines.size() == 0) {
            DCApiManager.getInstance().postRoutine(routine, new DCResponseListener<DCRoutine>() {
                @Override
                public void onFailure(DCError error) {
                    if (error != null && error.isType(DCErrorType.NETWORK)) {
                        synchronized (routines) {
                            routines.add(routine);
                            saveRoutines();
                            notifyObserversOnSyncNeeded(routines.toArray(new DCRoutine[routines.size()]));
                        }
                    } else {
                        notifyObserversOnError(error);
                    }

                    if (listener != null)
                        listener.onFailure(error);
                }

                @Override
                public void onResponse(DCRoutine object) {
                    updateDashboard(true);
                    if (listener != null)
                        listener.onResponse(object);
                }
            });
        } else {
            routines.add(routine);
            notifyObserversOnSyncNeeded(routines.toArray(new DCRoutine[routines.size()]));
        }
    }

    /**
     * Get latest journey data
     */
    public void updateJourney(boolean hard) {
        if (!hard && journey != null) {
            for (IDCDashboardObserver observer : dashboardObservers) {
                observer.onJourneyUpdated(journey);
            }
        } else {
            if (inRequestJourney)
                return;

            inRequestJourney = true;
            DCApiManager.getInstance().getJourney(new DCResponseListener<DCJourney>() {
                @Override
                public void onFailure(DCError error) {
                    for (IDCDashboardObserver observer : dashboardObservers) {
                        observer.onJourneyError(error);
                    }
                    inRequestJourney = false;
                }

                @Override
                public void onResponse(DCJourney object) {
                    DCDashboardDataProvider.this.journey = object;
                    for (IDCDashboardObserver observer : dashboardObservers) {
                        observer.onJourneyUpdated(object);
                    }
                    inRequestJourney = false;
                }
            });
        }
    }

    /**
     * Try to sync all routines with the server
     * @param silent
     */
    public void sync(final boolean silent) {
        synchronized (routines) {
            if (routines.size() > 0) {
                final DCRoutine routine = routines.get(0);
                DCApiManager.getInstance().postRoutine(routine, new DCResponseListener<DCRoutine>() {
                    @Override
                    public void onFailure(DCError error) {
                        if (!silent)
                            notifyObserversOnError(error);

                        if (error != null && !error.isType(DCErrorType.NETWORK)) {
                            synchronized (routines) {
                                routines.remove(routine);
                                saveRoutines();
                                sync(silent);
                            }
                        }
                    }

                    @Override
                    public void onResponse(DCRoutine object) {
                        if (object != null) {
                            routines.remove(routine);
                            saveRoutines();
                            if (!silent)
                                notifyObserversOnSyncSuccess();

                            sync(silent);
                        }
                    }
                });
            }
        }
    }

    /**
     * Notify all observers when the dashboard was updated
     */
    private void notifyObserversOnDashboardUpdated() {
        if (dashboard != null) {
            for (IDCDashboardObserver observer : dashboardObservers) {
                observer.onDashboardUpdated(dashboard);
            }
        }
    }

    /**
     * Notify all observers when an error occurred
     * @param error
     */
    private void notifyObserversOnError(DCError error) {
        for (IDCDashboardObserver observer : dashboardObservers) {
            observer.onDashboardError(error);
        }
    }

    /**
     * Notify all observers when there are routines that need to be synced
     * @param needSync
     */
    private void notifyObserversOnSyncNeeded(DCRoutine[] needSync) {
        for (IDCDashboardObserver observer : dashboardObservers) {
            observer.onSyncNeeded(needSync);
        }
    }

    /**
     * Notify all observers when all routines were synced
     */
    private void notifyObserversOnSyncSuccess() {
        for (IDCDashboardObserver observer : dashboardObservers) {
            observer.onSyncSuccess();
        }
    }

    /**
     * Load dashboard from preferences
     */
    private void loadDashboard() {
        String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.DASHBOARD);
        if (json != null) {
            try {
                dashboard = DCApiManager.gson.fromJson(json, DCDashboard.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save dashboard to preferences
     */
    private void saveDashboard() {
        if (dashboard != null) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.DASHBOARD, DCApiManager.gson.toJson(dashboard));
        }
    }

    /**
     * Load failed routines from preferences
     */
    private void loadRoutines() {
        synchronized (routines) {
            String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.ROUTINES);
            if (json != null) {
                try {
                    routines.clear();
                    DCRoutine[] routinesArray = DCApiManager.gson.fromJson(json, new TypeToken<DCRoutine[]>(){}.getType());
                    if (routinesArray != null) {
                        routines.addAll(Arrays.asList(routinesArray));
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Save failed routines to preferences
     */
    private void saveRoutines() {
        synchronized (routines) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.ROUTINES, DCApiManager.gson.toJson(routines.toArray()));
        }
    }

    /**
     * Clear current dashboard & routines
     * Use this on logout
     */
    public void clear() {
        dashboardObservers.clear();
        journey = null;
        dashboard = null;
        synchronized (routines) {
            routines.clear();
        }
    }
}