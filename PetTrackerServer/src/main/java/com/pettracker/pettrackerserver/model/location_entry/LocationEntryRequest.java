package com.pettracker.pettrackerserver.model.location_entry;

import javax.persistence.Id;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

public class LocationEntryRequest {

    private String username;
    private String device_name;
    private String device_password;
    private double latitude;
    private double longitude;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getDevice_password() {
		return device_password;
	}
	public void setDevice_password(String device_password) {
		this.device_password = device_password;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}