package com.dentacoin.dentacare.model;

import com.dentacoin.dentacare.utils.DCConstants;

import java.io.Serializable;

/**
 * Created by Atanas Chervarov on 10/4/17.
 */

public class DCGoal implements Serializable {
    private String id;
    private String title;
    private String description;
    private int reward;
    private boolean completed;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getReward() { return reward; }
    public boolean isCompleted() { return completed; }
    private Integer amount;

    public DCConstants.DCGoalType getType() {
        if (id != null && id.length() > 0) {
            if (id.contains("week") || id.contains("day")) {
                return DCConstants.DCGoalType.WEEK;
            } else if (id.contains("month")) {
                return DCConstants.DCGoalType.MONTH;
            } else if (id.contains("year")) {
                return DCConstants.DCGoalType.YEAR;
            }
        }
        return DCConstants.DCGoalType.DEFAULT;
    }

    public Integer getAmount() {
        if (amount != null)
            return amount;

        if (id != null && id.length() > 0) {
            String[] result = id.split("_");
            if (result.length > 0) {
                String last = result[result.length - 1];
                try {
                    amount = Integer.parseInt(last);
                    return amount;
                } catch (NumberFormatException e) {

                }
            }
        }
        return null;
    }
}
