package edu.ktu.pettrackerclient.pets;

import android.util.Log;

public class Pet {
    private Long id;
    private String name;
    private Long fk_device_id;
    private Long fk_user_id;
    private Long fk_zone_id;
    private boolean notifications;
    private String picture;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        Log.d("1122", "inside setter "+ String.valueOf(notifications));

        this.notifications = notifications;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pet)) {
            return false;
        }
        Pet p = (Pet) o;
        if(!equalsWithNulls(p.getId(),  this.id))  {
            return false;
        }
        if(!equalsWithNulls(p.getName(), this.name))  {
            return false;
        }
        if(!equalsWithNulls(p.getFk_device_id(), this.fk_device_id))  {
            return false;
        }
        if(!equalsWithNulls(p.getFk_user_id(), this.fk_user_id))  {
            return false;
        }
        if(!equalsWithNulls(p.isNotifications(), this.notifications))  {
            return false;
        }

        if(!equalsWithNulls(p.getFk_zone_id(), this.fk_zone_id))  {
            return false;
        }

        if(!equalsWithNulls(p.getPicture(), this.picture))  {
            return false;
        }
        return true;
    }

    public static final boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
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

    public Long getFk_device_id() {
        return fk_device_id;
    }

    public void setFk_device_id(Long fk_device_id) {
        this.fk_device_id = fk_device_id;
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

    public Pet() {

    }



}
