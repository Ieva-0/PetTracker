package edu.ktu.pettrackerclient.zones;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Zone {
    public Long id;
    public String name;
    public Long fk_user_id;

    public Long getFk_user_id() {
        return fk_user_id;
    }
    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
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

}
