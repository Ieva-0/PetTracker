package edu.ktu.pettrackerclient.model;

import java.sql.Blob;

public class Pet {
    private Long id;
    private String name;
    private Long fk_device_id;
    private String photo;
    private Long fk_user_id;

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fk_device_id=" + fk_device_id +
                ", fk_user_id=" + fk_user_id +
                ", photo=" + photo +
                '}';
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

    public Long getFk_device_id() {
        return fk_device_id;
    }

    public void setFk_device_id(Long fk_device_id) {
        this.fk_device_id = fk_device_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public Long getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(Long fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public Pet() {

    }
}
