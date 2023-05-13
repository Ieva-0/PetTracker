package com.pettracker.pettrackerserver.users.models;

public class ChangePasswordRequest {
    String old_pw;
    String new_pw;
    public ChangePasswordRequest() {

    }

    public String getOld_pw() {
        return old_pw;
    }

    public void setOld_pw(String old_pw) {
        this.old_pw = old_pw;
    }

    public String getNew_pw() {
        return new_pw;
    }

    public void setNew_pw(String new_pw) {
        this.new_pw = new_pw;
    }
}
