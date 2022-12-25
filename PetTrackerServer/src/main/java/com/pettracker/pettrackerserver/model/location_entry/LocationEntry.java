package com.pettracker.pettrackerserver.model.location_entry;

import javax.persistence.Id;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class LocationEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double lattitude;
    private double longitude;
    private long used_at;
    private long created_at;

    @Override
    public String toString() {
        return "LocationEntry{" +
                "id=" + id +
                ", lattitude=" + lattitude +
                ", longitude=" + longitude +
                ", used_at=" + used_at +
                ", created_at=" + created_at +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getUsed_at() {
        return used_at;
    }

    public void setUsed_at(long used_at) {
        this.used_at = used_at;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}