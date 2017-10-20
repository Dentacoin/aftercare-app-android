package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 10/5/17.
 */

public class DCGoalsDataProvider {

    private static DCGoalsDataProvider instance;

    private ArrayList<DCGoal> goals;
    private ArrayList<IDCGoalsObserver> goalsObservers;
    private boolean inRequest = false;

    public static synchronized DCGoalsDataProvider getInstance() {
        if (instance == null)
            instance = new DCGoalsDataProvider();

        return instance;
    }

    private DCGoalsDataProvider() {
        goalsObservers = new ArrayList<>();
        goals = new ArrayList<>();
        loadGoals();
    }

    public void updateGoals(boolean hard) {
        loadGoals();
        if (!hard) {
            notifyOnGoalsUpdated();
        } else {
            if (inRequest)
                return;

            inRequest = true;

            DCApiManager.getInstance().getGoals(new DCResponseListener<DCGoal[]>() {
                @Override
                public void onFailure(DCError error) {
                    inRequest = false;
                    notifyOnGoalsError(error);
                }

                @Override
                public void onResponse(DCGoal[] object) {
                    inRequest = false;

                    if (object != null) {
                        goals = new ArrayList<DCGoal>(Arrays.asList(object));
                        saveGoals();
                    }

                    notifyOnGoalsUpdated();

                    checkGoals();
                }
            });
        }
    }

    private void loadGoals() {
        String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.GOALS);
        if (json != null) {
            try {
                DCGoal[] _goals = DCApiManager.gson.fromJson(json, DCGoal[].class);
                if (_goals != null) {
                    goals = new ArrayList<>(Arrays.asList(_goals));
                }
            } catch (JsonSyntaxException e) {
            }
        }
    }

    private void saveGoals() {
        if (goals != null) {
            DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.GOALS, DCApiManager.gson.toJson(goals.toArray()));
        }
    }

    public void addObserver(IDCGoalsObserver observer) {
        if (!goalsObservers.contains(observer)) {
            goalsObservers.add(observer);
        }
    }

    public void removeObserver(IDCGoalsObserver observer) {
        goalsObservers.remove(observer);
    }

    private void notifyOnGoalsUpdated() {
        for (IDCGoalsObserver observer : goalsObservers) {
            observer.onGoalsUpdated(goals);
        }
    }

    private void notifyOnGoalAchieved(DCGoal goal) {
        for (IDCGoalsObserver observer : goalsObservers) {
            observer.onGoalAchieved(goal);
        }
    }

    private void notifyOnGoalsError(DCError error) {
        for (IDCGoalsObserver observer : goalsObservers) {
            observer.onGoalsError(error);
        }
    }

    private void checkGoals() {
        if (goals != null && goals.size() > 0) {
            ArrayList<String> reachedGoals;

            String goalsJson = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.GOALS_REACHED);

            if (goalsJson == null) {
                reachedGoals = new ArrayList<>();
                for (DCGoal goal : goals) {
                    if (goal.isCompleted()) {
                        reachedGoals.add(goal.getId());
                    }
                }

                DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.GOALS_REACHED, DCApiManager.gson.toJson(reachedGoals.toArray()));
            } else {
                try {
                    String[] reached = DCApiManager.gson.fromJson(goalsJson, String[].class);
                    if (reached != null) {
                        reachedGoals = new ArrayList<>(Arrays.asList(reached));
                        for (DCGoal goal : goals) {
                            if (goal.isCompleted()) {
                                boolean alreadyReached = false;
                                for (String goalId : reachedGoals) {
                                    if (goalId.compareTo(goal.getId()) == 0) {
                                        alreadyReached = true;
                                        break;
                                    }
                                }

                                if (!alreadyReached) {
                                    //Notify on the first achieved goal only
                                    reachedGoals.add(goal.getId());
                                    DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.GOALS_REACHED, DCApiManager.gson.toJson(reachedGoals.toArray()));
                                    notifyOnGoalAchieved(goal);
                                    break;
                                }
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
