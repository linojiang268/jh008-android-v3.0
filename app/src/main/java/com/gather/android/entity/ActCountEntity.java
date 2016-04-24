package com.gather.android.entity;

/**
 * Created by Levi on 2015/8/10.
 */
public class ActCountEntity {
    /**
     * all : 2
     * waitPay : 2
     * end : 0
     * notBeginning : 0
     */
    private int all;
    private int waitPay;
    private int end;
    private int notBeginning;

    public void setAll(int all) {
        this.all = all;
    }

    public void setWaitPay(int waitPay) {
        this.waitPay = waitPay;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setNotBeginning(int notBeginning) {
        this.notBeginning = notBeginning;
    }

    public int getAll() {
        return all;
    }

    public int getWaitPay() {
        return waitPay;
    }

    public int getEnd() {
        return end;
    }

    public int getNotBeginning() {
        return notBeginning;
    }
}
