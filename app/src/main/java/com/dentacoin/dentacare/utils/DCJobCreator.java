package com.dentacoin.dentacare.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Atanas Chervarov on 10/16/17.
 */

public class DCJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        if (DCNotificationJob.NOTIFICATION.DAILY_BRUSHING.withTag(tag) ||
            DCNotificationJob.NOTIFICATION.CHANGE_BRUSH.withTag(tag)) {
            return new DCNotificationJob();
        }
        return null;
    }
}
