package com.dentacoin.dentacare.model;

import android.content.Context;

import com.dentacoin.dentacare.network.DCSession;
import com.google.gson.annotations.Expose;
import com.mukesh.countrypicker.Country;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.dentacoin.dentacare.utils.DCConstants.DATE_FORMAT_USER_BIRTHDAY;
import static com.dentacoin.dentacare.utils.DCConstants.GENDER_FEMALE;
import static com.dentacoin.dentacare.utils.DCConstants.GENDER_MALE;
import static com.dentacoin.dentacare.utils.DCConstants.GENDER_UNSPECIFIED;

/**
 * Created by Atanas Chervarov on 8/7/17.
 */

public class DCUser implements Serializable {
    @Expose(serialize = false, deserialize = true)
    private int id;
    @Expose() private String email;
    @Expose() private String password;
    @Expose() private String firstname;
    @Expose() private String lastname;
    @Expose() private String birthday;
    @Expose() private Integer postalCode;
    @Expose() private String country;
    @Expose() private String city;
    @Expose() private String gender;
    @Expose() private DCAvatar avatar;
    @Expose() private String avatar_64;
    @Expose() private String facebookID;
    @Expose() private String facebookAccessToken;
    @Expose() private String googleID;
    @Expose() private String googleAccessToken;
    @Expose() private String twitterID;
    @Expose() private String twitterAccessToken;
    @Expose() private String twitterAccessTokenSecret;
    @Expose() private Integer captchaId;
    @Expose() private String captchaCode;
    @Expose() private boolean consent;
    @Expose(serialize = false, deserialize = true) private boolean confirmed = false;
    @Expose(serialize = false, deserialize = true) private String accountType;
    @Expose(serialize = false, deserialize = true) private boolean isChild;

    //TODO: fix!
    public boolean isChild() {
        return isChild;
    }

    public void setHasConsent(boolean consent) {
        this.consent = consent;
    }

    public boolean hasConsent() {
        return consent;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setCaptchaId(int captchaId) { this.captchaId = captchaId; }

    public void setCaptchaCode(String captchaCode) { this.captchaCode = captchaCode; }

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

    public void setBirthday(Date date) {
        if (date != null) {
            this.birthday = DATE_FORMAT_USER_BIRTHDAY.format(date);
        } else {
            this.birthday = null;
        }
    }

    public Date getBirthday() {
        if (birthday != null) {
            try {
                return DATE_FORMAT_USER_BIRTHDAY.parse(birthday);
            } catch (ParseException e) {

            }
        }
        return null;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getPostalCode() { return postalCode; }

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

    public String getGender() {
        return gender;
    }

    public void setAvatar_64(String avatar_64) {
        this.avatar_64 = avatar_64;
    }

    public String getAvatar_64() {
        return avatar_64;
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

    public String getEmail() { return email; }

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

    public DCAvatar getAvatar() {
        return avatar;
    }

    public String getAvatarUrl(Context context) {
        if (avatar != null) {
            return avatar.getAvatarUrl(context);
        }

        return DCSession.getInstance().getCurrentUserSocialAvatar();
    }

    /**
     * Retrieve users full address
     * @return  null if none of the address was setup
     */
    public String getFullAddress() {
        String address = "";

        if (postalCode != null) {
            address += postalCode;
            address += " ";
        }

        if (city == null && country == null)
            return null;
        else {
            if (city != null) {
                address += city;
            }
            if (country != null) {
                address += ", ";
                address += country;
            }
        }

        return address;
    }

    /**
     * Retrieve users age in years
     * @return  null if user did not set his burthday
     */
    public Integer getAge() {
        if (getBirthday() != null) {
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(new Date());

            Calendar birthdayCalendar = Calendar.getInstance();
            birthdayCalendar.setTime(getBirthday());

            int yearDiff = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

            if (birthdayCalendar.get(Calendar.MONTH) > nowCalendar.get(Calendar.MONTH) ||
                birthdayCalendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH) &&
                birthdayCalendar.get(Calendar.DATE) > nowCalendar.get(Calendar.DATE)) {
                yearDiff --;
            }

            return yearDiff;
        }

        return null;
    }

    /**
     * Get full locaiton
     * @return  null if city & country are not set
     */
    public String getLocation() {
        if (city != null && country != null) {
            Country co = Country.getCountryByISO(country);
            if (co != null) {
                return city + ", " + co.getName();
            }
        }
        return null;
    }
}
