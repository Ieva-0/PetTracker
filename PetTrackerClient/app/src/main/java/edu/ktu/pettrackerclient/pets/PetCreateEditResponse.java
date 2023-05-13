package edu.ktu.pettrackerclient.pets;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.devices.Device;
import edu.ktu.pettrackerclient.zones.Zone;

public class PetCreateEditResponse {
    List<Zone> zones;
    List<Device> devices;
    PetWithDetails pet;
    public PetCreateEditResponse() {
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

    @Override
    public String toString() {
        return "PetEditCreateResponse{" +
                "zones=" + zones +
                ", devices=" + devices +
                ", pet=" + pet +
                '}';
    }
}
