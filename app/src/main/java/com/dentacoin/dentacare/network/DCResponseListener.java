package com.dentacoin.dentacare.network;


import com.dentacoin.dentacare.model.DCError;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public interface DCResponseListener<T> {
    void onFailure(DCError error);
    void onResponse(T object);
}