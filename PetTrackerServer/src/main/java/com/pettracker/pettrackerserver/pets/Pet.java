package com.pettracker.pettrackerserver.pets;

import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long fk_device_id;
	private String photo;
	private Long fk_user_id;
	private Long fk_zone_id;
	private boolean notifications;

	public boolean isNotifications() {
		return notifications;
	}

	public void setNotifications(boolean notifications) {
		this.notifications = notifications;
	}

	public Pet() {

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

	public Long getFk_zone_id() {
		return fk_zone_id;
	}

	public void setFk_zone_id(Long fk_zone_id) {
		this.fk_zone_id = fk_zone_id;
	}

	@Override
	public String toString() {
		return "Pet{" + "id=" + id + ", name='" + name + '\'' + ", fk_device_id=" + fk_device_id + ", fk_user_id="
				+ fk_user_id + ", fk_zone_id=" + fk_zone_id + ", photo=" + photo + '}';
	}

}