package com.pettracker.pettrackerserver.zones;

import java.util.ArrayList;
import java.util.List;

import com.pettracker.pettrackerserver.pet_groups.PetGroup;
import com.pettracker.pettrackerserver.pets.Pet;

public class ZonesForDeviceResponse {
    public Pet pet;
    public ZoneWithPoints assignedToPet;
    public List<ZoneWithPoints> assignedToGroups;
    public List<PetGroup> groups;
    public ZonesForDeviceResponse() {
        assignedToGroups = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public ZoneWithPoints getAssignedToPet() {
        return assignedToPet;
    }

    public void setAssignedToPet(ZoneWithPoints assignedToPet) {
        this.assignedToPet = assignedToPet;
    }

    public List<ZoneWithPoints> getAssignedToGroups() {
        return assignedToGroups;
    }

    public void setAssignedToGroups(List<ZoneWithPoints> assignedToGroups) {
        this.assignedToGroups = assignedToGroups;
    }

    public List<PetGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<PetGroup> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "ZonesForDeviceResponse{" +
                "pet=" + pet +
                ", assignedToPet=" + assignedToPet +
                ", assignedToGroups=" + assignedToGroups +
                ", groups=" + groups +
                '}';
    }

    
}