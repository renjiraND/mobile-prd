package com.renrairah.bukalock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class History {
    private int unlockType; //1 = Gesture, 2 = Games, 3 = Card
    private long date;
    private int status; //0 = Failed, 1 = Success

    public History(){}

    public History(int unlockType, long date, int status){
        this.unlockType = unlockType;
        this.date = date;
        this.status = status;
    }

    public History(History history){
        this.unlockType = history.getUnlockType();
        this.date = history.getDate();
        this.status = history.getStatus();
    }

    public int getUnlockType() {
        return unlockType;
    }

    public long getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public void setUnlockType(int unlockType) {
        this.unlockType = unlockType;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String dateToString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH.mm");
        String strDate = dateFormat.format(new Date(this.date));
        return strDate;
    }

    public String statusToString() {
        if (this.status == 0){
            return "Failed";
        } else {
            return "Success";
        }
    }
}
