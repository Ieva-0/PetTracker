package edu.ktu.pettrackerclient.zones;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.pets.Pet;

public class ZoneWithDetails {
    public Long id;
    public String name;
    public Long fk_user_id;
    public List<Pet> pets;
    public List<PetGroup> groups;

    public ZoneWithDetails() {
        pets = new ArrayList<>();
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

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<PetGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<PetGroup> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "ZoneWithDetails{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", pets=" + pets +
                ", groups=" + groups +
                '}';
    }
}
