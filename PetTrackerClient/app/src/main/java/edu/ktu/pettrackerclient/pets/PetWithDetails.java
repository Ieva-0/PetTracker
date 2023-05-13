package edu.ktu.pettrackerclient.pets;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.devices.Device;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.zones.Zone;

public class PetWithDetails {

    private Long id;
    private String name;
    private Long fk_user_id;
    private Zone zone;
    private Device device;
    private boolean notifications;
    private String picture;
    private List<PetGroup> groups;

    public PetWithDetails() {
        groups = new ArrayList<>();
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

    public Long getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<PetGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<PetGroup> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "PetWithDetails{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", zone=" + zone +
                ", device=" + device +
                ", notifications=" + notifications +
                ", picture='" + picture + '\'' +
                ", groups=" + groups +
                '}';
    }
}
