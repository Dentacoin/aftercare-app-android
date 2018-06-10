package com.dentacoin.dentacare.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.utils.DCErrorType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Atanas Chervarov on 8/7/17.
 * Helper class for handling & parsing the API responses
 */

public class DCResponseHandler<T> implements Callback {

    private Class<T> clazz;
    private DCResponseListener<T> responseListener;

    DCResponseHandler(DCResponseListener<T> responseListener, @NonNull Class<T> clazz) {
        this.clazz = clazz;
        this.responseListener = responseListener;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (responseListener != null)
                responseListener.onFailure(new DCError(DCErrorType.NETWORK));
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        if (responseListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            if (response.body() != null) {
                try {
                    final String jsonString = response.body().string();
                    Log.d("RESPONSE", jsonString);

                    handler.post(() -> {
                        int jsonCode = 200;
                        try {
                            JsonObject jsonObject = DCApiManager.gson.fromJson(jsonString, JsonObject.class);
                            if (jsonObject != null) {
                                JsonElement element = jsonObject.get("code");
                                if (element != null) {
                                    String responseCode = element.toString();
                                    jsonCode = Integer.valueOf(responseCode);
                                }
                            }
                        } catch (JsonSyntaxException | IllegalStateException | NumberFormatException e) {
//                            e.printStackTrace();
                        }

                        try {
                            if (response.code() >= 200 && response.code() < 300 && jsonCode >= 200 && jsonCode < 300) {
                                T object = DCApiManager.gson.fromJson(jsonString, clazz);
                                responseListener.onResponse(object);
                            } else {
                                DCError error = DCApiManager.gson.fromJson(jsonString, DCError.class);
                                responseListener.onFailure(error);
                            }
                        } catch (JsonSyntaxException | IllegalStateException e) {
                            responseListener.onFailure(new DCError(DCErrorType.JSONSYNTAX));
                        }
                    });
                } catch (NullPointerException e) {
                    handler.post(() -> {
                        responseListener.onFailure(new DCError(DCErrorType.UNKNOWN));
                    });
                }
            }
        }
    }
}
