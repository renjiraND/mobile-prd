package com.renrairah.bukalock;

import com.firebase.ui.auth.data.model.User;

public class Users {
    private String email;
    private String rfid;

    public Users(){}

    public Users(String email, String rfid){
        this.email = email;
        this.rfid = rfid;
    }

    public Users(Users user){
        this.email = user.getEmail();
        this.rfid = user.getRfid();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
