package com.dentacoin.dentacare.model;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 10.05.18.
 */
public class DCChild {
    @Expose(serialize = false, deserialize = true)
    private int id;
    @Expose()
    private String firstname;
    @Expose()
    private int birthyear;
    @Expose(serialize = false, deserialize = true)
    private Date lastactivity;

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public Date getLastactivity() {
        return lastactivity;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }
}
