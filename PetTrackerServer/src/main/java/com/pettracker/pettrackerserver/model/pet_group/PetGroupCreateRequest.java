package com.pettracker.pettrackerserver.model.pet_group;

import java.util.List;

import com.pettracker.pettrackerserver.model.pet.Pet;

public class PetGroupCreateRequest {
    private Long group_id;
    private String group_name;
    private List<Pet> pets;
    private Long zone_id;
    private boolean notifications;


    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
    public Long getZone_id() {
        return zone_id;
    }

    public void setZone_id(Long zone_id) {
        this.zone_id = zone_id;
    }
    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
}