package com.dentacoin.dentacare.utils;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 10/12/17.
 */

public class DCTutorialManager {
    private static DCTutorialManager instance;
    private ArrayList<Tutorial> tutorials;
    private ArrayList<IDCTutorial> observers;

    public static synchronized DCTutorialManager getInstance() {
        if (instance == null)
            instance = new DCTutorialManager();

        return instance;
    }

    private DCTutorialManager() {
        tutorials = new ArrayList<>();
        observers = new ArrayList<>();
        tutorials.add(Tutorial.TOTAL_DCN);
        tutorials.add(Tutorial.LAST_ACTIVITY_TIME);
        tutorials.add(Tutorial.LEFT_ACTIVITIES_COUNT);
        tutorials.add(Tutorial.DCN_EARNED);
        tutorials.add(Tutorial.DASHBOARD_STATISTICS);
        tutorials.add(Tutorial.EDIT_PROFILE);
        tutorials.add(Tutorial.COLLECT_DCN);
        tutorials.add(Tutorial.WITHDRAWS);
        tutorials.add(Tutorial.GOALS);
        tutorials.add(Tutorial.EMERGENCY_MENU);
    }

    public void subscribe(IDCTutorial observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(IDCTutorial observer) {
        observers.remove(observer);
    }

    public void showNext() {
        if (observers.size() > 0) {
            for (Tutorial tutorial : tutorials) {
                if (!DCSharedPreferences.getShownTutorials().contains(tutorial.name())) {
                    for (IDCTutorial observer : observers) {
                        observer.showTutorial(tutorial);
                    }
                    return;
                }
            }
        }
    }
}