package com.dentacoin.dentacare.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 11.05.18.
 */
public class DCSection<T> {
    private int titleResource;
    private ArrayList<T> data;

    public DCSection() {
    }

    public DCSection(int titleResource, ArrayList<T> data) {
        this.titleResource = titleResource;
        this.data = data;
    }

    public void setTitle(int titleResource) {
        this.titleResource = titleResource;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    public String getTitle(Context context) {
        if (titleResource != 0)
            return context.getString(titleResource);
        return null;
    }

    public int getTitle() {
        return titleResource;
    }

    public ArrayList<T> getData() {
        return data;
    }
}
