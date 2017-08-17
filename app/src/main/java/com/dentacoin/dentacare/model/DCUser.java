package com.dentacoin.dentacare.model;

import android.content.Context;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public class DCUser {

    public final static String GENDER_MALE = "male";
    public final static String GENDER_FEMALE = "female";
    public final static String GENDER_UNSPECIFIED = "unspecified";

    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private Date birthday;
    private int postalCode;
    private String country;
    private String city;
    private String gender;
    private DCAvatar avatar;
    private String facebookID;
    private String facebookAccessToken;
    private String googleID;
    private String googleAccessToken;
    private String twitterID;
    private String twitterAccessToken;
    private String twitterAccessTokenSecret;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(String gender) {
        if (gender != null) {
            if (GENDER_MALE.equals(gender)) {
                this.gender = GENDER_MALE;
            } else if (GENDER_FEMALE.equals(gender)) {
                this.gender = GENDER_FEMALE;
            } else {
                this.gender = GENDER_UNSPECIFIED;
            }
        } else {
            this.gender = gender;
        }
    }

    public void setAvatar(DCAvatar avatar) {
        this.avatar = avatar;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public void setTwitterID(String twitterID) {
        this.twitterID = twitterID;
    }

    public void setTwitterAccessToken(String twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
    }

    public void setTwitterAccessTokenSecret(String twitterAccessTokenSecret) {
        this.twitterAccessTokenSecret = twitterAccessTokenSecret;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullName() {
        String name = "";

        if (firstname != null)
            name += firstname;

        if (lastname != null)
            name +=" " + lastname;

        return name;
    }

    public String getAvatarUrl(Context context) {
        if (avatar != null) {
            return avatar.getAvatarUrl(context);
        }

        return null;
    }
}
