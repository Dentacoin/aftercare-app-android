package com.dentacoin.dentacare.model;

import java.util.Date;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCTransaction {

    public enum STATUS {
        PENDING,
        APPROVED,
        DECLINED,
        UNKNOWN
    }

    private int amount;
    private String wallet;
    private String status;
    private Date date;

    public int getAmount() { return amount; }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
    public String getWallet() { return wallet; }

    public STATUS getStatus() {
        if (status != null) {
            if (status.equals("approved")) {
                return STATUS.APPROVED;
            }
            else if (status.equals("declined")) {
                return STATUS.DECLINED;
            }
            else if (status.equals("pending approval")) {
                return STATUS.PENDING;
            }
        }

        return STATUS.UNKNOWN;
    }

    public Date getDate() {
        return date;
    }
}
