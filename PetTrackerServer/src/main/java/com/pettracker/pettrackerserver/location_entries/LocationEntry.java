package com.pettracker.pettrackerserver.location_entries;

import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class LocationEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double latitude;
	private double longitude;
	private Long used_at;
	private Long created_at;
	private Long fk_device_id;

	@Override
	public String toString() {
		return "LocationEntry{" + "id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + ", used_at="
				+ used_at + ", created_at=" + created_at + '}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getFk_device_id() {
		return fk_device_id;
	}

	public void setFk_device_id(Long fk_device_id) {
		this.fk_device_id = fk_device_id;
	}
}