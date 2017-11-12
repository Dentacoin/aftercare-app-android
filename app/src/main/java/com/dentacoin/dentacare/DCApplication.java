package com.dentacoin.dentacare;

import android.app.Application;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.twitter.sdk.android.core.Twitter;

/**
 * Created by Atanas Chervarov on 7/31/17.
 */

public class DCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Twitter.initialize(this);
        DCSharedPreferences.initialize(this);
    }
}
