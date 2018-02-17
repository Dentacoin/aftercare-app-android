package com.dentacoin.dentacare.network.request;

/**
 * Created by Atanas Chervarov on 17.02.18.
 */

public class DCCaptcha {
    private String captchaCode;
    private int captchaId;

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public void setCaptchaId(int captchaId) {
        this.captchaId = captchaId;
    }
}
