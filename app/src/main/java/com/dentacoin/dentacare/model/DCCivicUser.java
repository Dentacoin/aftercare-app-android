package com.dentacoin.dentacare.model;

import android.support.annotation.NonNull;

import com.civic.connect.library.model.DataItem;
import com.civic.connect.library.model.UserData;
import com.dentacoin.dentacare.utils.DCConstants;

import java.text.ParseException;
import java.util.Date;

import static com.dentacoin.dentacare.utils.DCConstants.DATE_FORMAT_CIVIC_USER;

/**
 * Created by Atanas Chervarov on 16.10.18.
 */
public class DCCivicUser {

    private String userId;
    private String fullName;
    private String sex;
    private String dateOfBirth;
    private String country;
    private String email;
    private String phoneNumber;

    public DCCivicUser(@NonNull UserData userData) {
        userId = userData.getUserId();

        for (DataItem item : userData.getData()) {
            if (item.getLabel().compareToIgnoreCase("documents.genericId.name") == 0) {
                fullName = item.getValue();
            } else if (item.getLabel().compareToIgnoreCase("documents.genericId.sex") == 0) {
                sex = item.getValue();
            } else if (item.getLabel().compareToIgnoreCase("documents.genericId.dateOfBirth") == 0) {
                dateOfBirth = item.getValue();
            } else if (item.getLabel().compareToIgnoreCase("documents.genericId.country") == 0) {
                country = item.getValue();
            } else if (item.getLabel().compareToIgnoreCase("contact.personal.email") == 0) {
                email = item.getValue();
            } else if (item.getLabel().compareToIgnoreCase("contact.personal.phoneNumber") == 0) {
                phoneNumber = item.getValue();
            }
        }
    }


    public String getUserId() { return userId; }

    public String getFullName() { return fullName; }

    public String getCountry() { return country; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }


    public String getFirstName() {
        if (fullName != null && fullName.length() > 0) {
            String[] names = fullName.split(" ");
            if (names.length > 0) {
                return names[0];
            }
        }
        return null;
    }

    public String getLastName() {
        if (fullName != null && fullName.length() > 0) {
            String[] names = fullName.split(" ");
            if (names.length > 0) {
                return names[names.length - 1];
            }
        }
        return null;
    }

    public String getSex() {
        if (sex != null && sex.length() > 0) {
            if (sex.compareToIgnoreCase("M") == 0) {
                return DCConstants.GENDER_MALE;
            } else if (sex.compareToIgnoreCase("F") == 0) {
                return DCConstants.GENDER_FEMALE;
            }
        }
        return null;
    }

    public Date getDateOfBirth() {
        if (dateOfBirth != null) {
            try {
                Date date = DATE_FORMAT_CIVIC_USER.parse(dateOfBirth);
                return date;
            } catch (ParseException e) { }
        }
        return null;
    }

    public DCUser getUserObj() {
        DCUser user = new DCUser();
        user.setCivicUserId(getUserId());
        user.setFirstname(getFirstName());
        user.setLastname(getLastName());
        user.setEmail(getEmail());
        user.setBirthday(getDateOfBirth());
        user.setGender(getSex());
        user.setCountry(getCountry());
        return user;
    }
}
