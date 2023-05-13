package edu.ktu.pettrackerclient.devices;

import edu.ktu.pettrackerclient.pets.Pet;

public class DeviceWithDetails {
    private Long id;
    private String name;
    private String password;
    private Long fk_user_id;
    private Pet pet;

    public DeviceWithDetails() {

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "DeviceWithDetails{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", pet=" + pet +
                '}';
    }
}
