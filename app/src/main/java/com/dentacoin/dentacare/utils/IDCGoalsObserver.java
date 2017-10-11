package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGoal;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 10/5/17.
 */

public interface IDCGoalsObserver {
    void onGoalsUpdated(ArrayList<DCGoal> goals);
    void onGoalAchieved(DCGoal goal);
    void onGoalsError(DCError error);
}
