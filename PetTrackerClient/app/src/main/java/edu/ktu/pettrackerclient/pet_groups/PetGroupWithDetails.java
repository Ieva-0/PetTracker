package edu.ktu.pettrackerclient.pet_groups;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.zones.Zone;

public class PetGroupWithDetails {
    private Long id;
    private String name;
    private Long fk_user_id;
    private boolean notifications;
    private Zone zone;
    private List<Pet> pets;
    public PetGroupWithDetails() {
        pets = new ArrayList<>();
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

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    @Override
    public String toString() {
        return "PetGroupWithDetails{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", notifications=" + notifications +
                ", zone=" + zone +
                ", pets=" + pets +
                '}';
    }
}
