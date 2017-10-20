package com.dentacoin.dentacare.model;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCTransaction {
    private int amount;
    private String wallet;

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
