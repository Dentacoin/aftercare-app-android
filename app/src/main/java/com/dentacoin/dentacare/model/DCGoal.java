package com.dentacoin.dentacare.model;

import android.net.Uri;

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

    public Uri getImageUri() {

        return null;
    }
}
