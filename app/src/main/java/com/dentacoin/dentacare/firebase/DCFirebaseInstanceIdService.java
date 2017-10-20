package com.dentacoin.dentacare.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Atanas Chervarov on 10/15/17.
 */

public class DCFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = DCFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FBM token refreshed:" + refreshedToken);
    }
}
