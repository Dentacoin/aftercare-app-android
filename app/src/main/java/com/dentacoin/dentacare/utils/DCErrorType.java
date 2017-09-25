package com.dentacoin.dentacare.utils;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 9/23/17.
 */

public enum DCErrorType {
    BAD_REQUEST(400, R.string.error_txt_something_went_wrong),
    UNAUTHORIZED(401, R.string.error_txt_something_went_wrong),
    FORBIDDEN(403, R.string.error_txt_something_went_wrong),
    NOT_FOUND(404, R.string.error_txt_something_went_wrong),
    NO_FACEBOOK_AUTHORIZATION(417, R.string.error_txt_something_went_wrong),
    SERVER(500, R.string.error_txt_something_went_wrong),
    JSONSYNTAX(1003, R.string.error_txt_json),
    UNKNOWN(1001, R.string.error_txt_something_went_wrong),
    NETWORK(1002, R.string.error_txt_offline);

    private int code;
    private int resource;

    DCErrorType(int code, int resource) {
        this.code = code;
        this.resource = resource;
    }

    public int getResource() { return resource; }
    public int getCode() { return code; }
}