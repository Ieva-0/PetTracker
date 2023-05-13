package edu.ktu.pettrackerclient.pet_groups;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.zones.Zone;

public class PetGroupCreateEditResponse {
    List<Pet> pets;
    List<Zone> zones;
    PetGroupWithDetails pet_group;
    public PetGroupCreateEditResponse() {
        zones = new ArrayList<>();
        pets = new ArrayList<>();
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public PetGroupWithDetails getPet_group() {
        return pet_group;
    }

    public void setPet_group(PetGroupWithDetails pet_group) {
        this.pet_group = pet_group;
    }

    @Override
    public String toString() {
        return "PetGroupCreateEditResponse{" +
                "pets=" + pets +
                ", zones=" + zones +
                ", pet_group=" + pet_group +
                '}';
    }
}
