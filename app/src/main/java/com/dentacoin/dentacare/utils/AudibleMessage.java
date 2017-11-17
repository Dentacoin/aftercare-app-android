package com.dentacoin.dentacare.utils;

import android.content.Context;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 11/16/17.
 */

public enum AudibleMessage {
    MORNING_GREETING(R.string.message_morning_routine_start, new Voice[] { Voice.MORNING_GREETING }),
    EVENING_GREETING(R.string.message_evening_routine_start, new Voice[] { Voice.EVENING_GREETING }),
    MORNING_ROUTINE_END(R.string.message_morning_routine_end, new Voice[] { Voice.RINSE_MORNING_DONE }),
    EVENING_ROUTINE_END(R.string.message_evening_routine_end, new Voice[] { Voice.RINSE_EVENING_DONE});

    AudibleMessage(int resourceId, Voice[] voices) {
        this.resourceId = resourceId;
        this.voices = voices;
    }

    int resourceId;
    Voice[] voices;

    public String getMessage(Context context) {
        return context.getResources().getString(resourceId);
    }

    public Voice[] getVoices() { return voices; }
}