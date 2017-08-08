package com.dentacoin.dentacare.network;


/**
 * Created by Atanas Chervarov on 8/3/17.
 */

public class DCSession {

    private static DCSession instance;

    public synchronized static DCSession getInstance() {
        if (instance == null)
            instance = new DCSession();

        return instance;
    }

    private DCSession() {
    }
}
