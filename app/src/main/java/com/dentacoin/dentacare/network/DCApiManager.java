package com.dentacoin.dentacare.network;

import android.net.Uri;

import com.dentacoin.dentacare.BuildConfig;
import com.dentacoin.dentacare.model.DCAvatar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Atanas Chervarov on 8/6/17.
 */

public class DCApiManager {
    private static final String TAG = DCApiManager.class.getSimpleName();
    private static DCApiManager instance;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private static final String ENDPOINT_UPLOAD_AVATAR = "upload_avatar";
    public static final Gson gson = new GsonBuilder().create();

    public static synchronized DCApiManager getInstance() {
        if (instance == null)
            instance = new DCApiManager();

        return instance;
    }

    private DCApiManager() {
    }

    private static String buildPath(String path, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(BuildConfig.HOST + path).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().toString();
    }

    /**
     * Uploads an avatar image
     * Returns either the created object or an error
     * @param avatar    Avatar file
     * @param responseListener  DCResponseListener for handling the response
     */
    public void uploadAvatar(File avatar,  DCResponseListener<DCAvatar> responseListener) {
        String endpoint = buildPath(ENDPOINT_UPLOAD_AVATAR, null);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", "avatar.jpg", RequestBody.create(MEDIA_TYPE_JPG, avatar))
                .build();

        client.newCall(new Request.Builder().url(endpoint).post(requestBody).build()).enqueue(new DCResponseHandler<>(responseListener, DCAvatar.class));
    }
}
