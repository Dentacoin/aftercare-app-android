package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCActivityRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.response.DCRecordsSyncResponse;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCDashboardDataProvider {

    private static DCDashboardDataProvider instance;

    private DCDashboard dashboard;
    private final List<DCActivityRecord> records = Collections.synchronizedList(new ArrayList<DCActivityRecord>());
    private ArrayList<IDCDashboardObserver> dashboardObservers;

    private boolean inRequest = false;

    public static synchronized DCDashboardDataProvider getInstance() {
        if (instance == null)
            instance = new DCDashboardDataProvider();

        return instance;
    }

    private DCDashboardDataProvider() {
        dashboardObservers = new ArrayList<>();
        loadDashboard();
        loadRecords();
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

                    synchronized (records) {
                        if (records.size() > 0)
                            notifyObserversOnSyncNeeded(records.toArray(new DCActivityRecord[records.size()]));
                    }
                }
            });
        }
    }

    /**
     * Tries to make an api call with the given record object, if there is no Network Connectivity at
     * the moment it is stored and will be handled later, When error occurs observers are notified
     * On successful api call the dashboard is updated
     *
     * @param record    Valid DCActivityRecord
     */
    public void addActivityRecord(final DCActivityRecord record) {
        if (record == null || record.getTime() < 30) {
            notifyObserversOnError(new DCError(R.string.dashboard_too_short_record));
            return;
        }

        if (record.getTime() > DCConstants.COUNTDOWN_MAX_AMOUNT + 5)
            return;

        DCApiManager.getInstance().postRecord(record, new DCResponseListener<DCActivityRecord>() {
            @Override
            public void onFailure(DCError error) {
                if (error != null && error.isType(DCErrorType.NETWORK)) {
                    synchronized (records) {
                        records.add(record);
                        saveRecords();
                        notifyObserversOnSyncNeeded(records.toArray(new DCActivityRecord[records.size()]));
                    }
                } else {
                    notifyObserversOnError(error);
                }
            }

            @Override
            public void onResponse(DCActivityRecord object) {
                updateDashboard(true);
            }
        });
    }

    /**
     * Try to sync all records with the server
     * @param silent
     */
    public void sync(final boolean silent) {
        synchronized (records) {
            if (records.size() > 0) {
                final DCActivityRecord[] cRecords = records.toArray(new DCActivityRecord[records.size()]);

                DCApiManager.getInstance().syncRecords(cRecords, new DCResponseListener<DCRecordsSyncResponse>() {
                    @Override
                    public void onFailure(DCError error) {
                        notifyObserversOnError(error);
                    }

                    @Override
                    public void onResponse(DCRecordsSyncResponse object) {
                        if (object != null) {
                            //TODO: clear only succeeded records
                            synchronized (records) {
                                records.clear();
                                saveRecords();
                                updateDashboard(true);

                                if (!silent)
                                    notifyObserversOnSyncSuccess();
                            }
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
     * Notify all observers when there are records that need to be synced
     * @param needSync
     */
    private void notifyObserversOnSyncNeeded(DCActivityRecord[] needSync) {
        for (IDCDashboardObserver observer : dashboardObservers) {
            observer.onSyncNeeded(needSync);
        }
    }

    /**
     * Notify all observers when all records were synced
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
            } catch (JsonSyntaxException e) { }
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
     * Load failed records from preferences
     */
    private void loadRecords() {
        synchronized (records) {
            String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.RECORDS);
            if (json != null) {
                try {
                    records.clear();
                    DCActivityRecord[] recordsArray = DCApiManager.gson.fromJson(json, new TypeToken<DCActivityRecord[]>(){}.getType());
                    if (recordsArray != null) {
                        records.addAll(Arrays.asList(recordsArray));
                    }
                } catch (JsonSyntaxException e) { }
            }
        }
    }

    /**
     * Save failed records to preferences
     */
    private void saveRecords() {
        synchronized (records) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.RECORDS, DCApiManager.gson.toJson(records.toArray()));
        }
    }

    /**
     * Clear current dashboard & records
     * Use this on logout
     */
    public void clear() {
        dashboardObservers.clear();

        synchronized (records) {
            records.clear();
        }
    }
}