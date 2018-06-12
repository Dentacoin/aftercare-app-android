package com.dentacoin.dentacare.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 10.05.18.
 */
public class DCChild implements Serializable {

    public DCChild() {
    }

    public DCChild(int id, String firstname, int birthyear) {
        this.id = id;
        this.firstname = firstname;
        this.birthyear = birthyear;
    }

    @Expose(serialize = false, deserialize = true)
    protected int id;
    @Expose()
    protected String firstname;
    @Expose()
    protected int birthyear;
    @Expose(serialize = false, deserialize = true)
    protected Date lastactivity;

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public Date getLastActivity() {
        return lastactivity;
    }

    public void setLastactivity(Date lastactivity) {
        this.lastactivity = lastactivity;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }
}
