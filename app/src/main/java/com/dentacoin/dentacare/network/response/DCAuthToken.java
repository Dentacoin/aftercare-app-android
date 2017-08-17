package com.dentacoin.dentacare.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/15/17.
 */

public class DCAuthToken {
    private String token;
    @SerializedName("token_valid_to")
    private Date validUntil;

    public String getToken() {
        return token;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public boolean isValid() {
        Date now = new Date();
        return token != null && validUntil != null && now.getTime() < validUntil.getTime();
    }
}
