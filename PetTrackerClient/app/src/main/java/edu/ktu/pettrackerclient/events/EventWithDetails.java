package edu.ktu.pettrackerclient.events;

import edu.ktu.pettrackerclient.devices.Device;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.zones.Zone;

public class EventWithDetails {
    private Long id;
    private Integer type;
    private Long timestamp;
    private Long fk_user_id;
    private Device device;
    private Pet pet;
    private Zone zone;
    private PetGroup pet_group;

    public EventWithDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public PetGroup getPet_group() {
        return pet_group;
    }

    public void setPet_group(PetGroup pet_group) {
        this.pet_group = pet_group;
    }

    @Override
    public String toString() {
        return "EventWithDetails{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", fk_user_id=" + fk_user_id +
                ", device=" + device +
                ", pet=" + pet +
                ", zone=" + zone +
                ", pet_group=" + pet_group +
                '}';
    }
}