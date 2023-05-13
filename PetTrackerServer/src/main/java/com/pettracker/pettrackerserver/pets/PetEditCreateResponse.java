package com.pettracker.pettrackerserver.pets;

import java.util.List;

import com.pettracker.pettrackerserver.devices.Device;
import com.pettracker.pettrackerserver.zones.Zone;

import java.util.ArrayList;

public class PetEditCreateResponse {
    List<Zone> zones;
    List<Device> devices;
    PetWithDetails pet;
    public PetEditCreateResponse() {
        zones = new ArrayList<>();
        devices = new ArrayList<>();
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public PetWithDetails getPet() {
        return pet;
    }

    public void setPet(PetWithDetails pet) {
        this.pet = pet;
    }
}