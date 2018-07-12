package com.dentacoin.dentacare.model;

/**
 * Created by Atanas Chervarov on 11.07.18.
 */
public class DCGasPrice {
    private long gasPrice;
    private long treshold;

    public long getGasPrice() {
        return gasPrice;
    }

    public long getTreshold() {
        return treshold;
    }

    public boolean isOverTreshold() {
        return gasPrice >= treshold;
    }
}
