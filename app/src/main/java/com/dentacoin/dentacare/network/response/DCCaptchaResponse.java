package com.dentacoin.dentacare.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Atanas Chervarov on 6.02.18.
 */

public class DCCaptchaResponse {
    @SerializedName("captcha_id")
    private int id;

    @SerializedName("captcha_image")
    private String image;

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
