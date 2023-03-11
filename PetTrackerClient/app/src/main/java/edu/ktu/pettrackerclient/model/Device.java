package edu.ktu.pettrackerclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Device  {
    private Long id;
    private String name;
    private String password;
    private Long fk_user_id;
    private Long fk_zone_id;

    public Device() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public Long getFk_zone_id() {
        return fk_zone_id;
    }

    public void setFk_zone_id(Long fk_zone_id) {
        this.fk_zone_id = fk_zone_id;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", fk_zone_id=" + fk_zone_id +
                '}';
    }
}
