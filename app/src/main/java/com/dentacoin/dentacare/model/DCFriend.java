package com.dentacoin.dentacare.model;

import android.content.Context;
import android.net.Uri;

import com.dentacoin.dentacare.utils.DCConstants;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 11.05.18.
 */
public class DCFriend {
    private int id;

    private String firstname;
    private String lastname;
    private String gender;
    private String email;

    private int birthyear;
    private Date birthday;

    private DCAvatar avatar;
    private String type;
    private Date lastActivity;
    private boolean needsAccept;

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public boolean needsAccept() {
        return needsAccept;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
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

    public Date getLastActivity() {
        return lastActivity;
    }
}
