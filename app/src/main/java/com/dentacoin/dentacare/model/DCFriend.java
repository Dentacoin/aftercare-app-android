package com.dentacoin.dentacare.model;

import android.content.Context;

import com.dentacoin.dentacare.utils.DCConstants;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 11.05.18.
 */
public class DCFriend extends DCChild implements Serializable {

    private String lastname;
    private String gender;
    private String email;
    private Date birthday;

    private DCAvatar avatar;
    private String type;
    private boolean needsAccept;

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public boolean needsAccept() {
        return needsAccept;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setNeedsAccept(boolean needsAccept) {
        this.needsAccept = needsAccept;
    }

    public String getType() {
        return type;
    }

    public boolean isFriend() {
        if (type != null)
            return type.compareTo(DCConstants.FRIEND_FRIEND) == 0;
        return false;
    }

    public boolean isFamily() {
        if (type != null)
            return type.compareTo(DCConstants.FRIEND_FAMILY) == 0;
        return false;
    }

    public boolean isChild() {
        if (type != null)
            return type.compareTo(DCConstants.FRIEND_CHILD) == 0;
        return false;
    }

    public String getFullName() {
        String name = "";
        if (firstname != null)
            name += firstname;
        if (lastname != null)
            name += " " + lastname;
        return name;
    }

    public String getAvatarUrl(Context context) {
        if (avatar != null) {
            return avatar.getAvatarUrl(context);
        }
        return null;
    }

    public Integer getAge() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        int yearDiff = 0;

        if (birthday != null) {
            Calendar birthdayCalendar = Calendar.getInstance();
            birthdayCalendar.setTime(birthday);

            yearDiff = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

            if (birthdayCalendar.get(Calendar.MONTH) > nowCalendar.get(Calendar.MONTH) ||
                    birthdayCalendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH) &&
                            birthdayCalendar.get(Calendar.DATE) > nowCalendar.get(Calendar.DATE)) {
                yearDiff --;
            }
        }
        else if (birthyear > 0) {
            Calendar birthYearCalendar = Calendar.getInstance();
            birthYearCalendar.set(Calendar.YEAR, birthyear);
            birthYearCalendar.set(Calendar.MONTH, 1);
            birthYearCalendar.set(Calendar.DAY_OF_MONTH, 1);
            yearDiff = nowCalendar.get(Calendar.YEAR) - birthYearCalendar.get(Calendar.YEAR);
        }

        if (yearDiff > 0)
            return yearDiff;

        return null;
    }
}
