package com.dentacoin.dentacare.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dentacoin.dentacare.model.DCError;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public class DCResponseHandler<T> implements Callback {

    private Class<T> clazz;
    private DCResponseListener<T> responseListener;

    DCResponseHandler(DCResponseListener<T> responseListener, @NonNull Class<T> clazz) {
        this.clazz = clazz;
        this.responseListener = responseListener;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if (responseListener != null)
            responseListener.onFailure(new DCError());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (responseListener != null) {
            if (response.body() != null) {
                try {
                    String jsonString = response.body().string();
                    Log.d("RESPONSE", jsonString);

                    if (response.code() == 200 || response.code() == 201) {
                        T object = DCApiManager.gson.fromJson(jsonString, clazz);
                        responseListener.onResponse(object);
                    } else {
                        responseListener.onFailure(new DCError());
                    }

                } catch (IOException | JsonSyntaxException | IllegalStateException e) {
                    responseListener.onFailure(new DCError());
                }
            }
        }
    }
}
