package com.renrairah.bukalock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class History {
    private int unlockType; //1 = Gesture, 2 = Games, 3 = Card
    private Date date;
    private int status; //0 = Failed, 1 = Success

    public History(int unlockType, Date date, int status){
        this.unlockType = unlockType;
        this.date = date;
        this.status = status;
    }

    public int getUnlockType() {
        return unlockType;
    }

    public Date getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public void setUnlockType(int unlockType) {
        this.unlockType = unlockType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String dateToString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH.mm");
        String strDate = dateFormat.format(this.date);
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
