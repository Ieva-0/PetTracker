package com.pettracker.pettrackerserver.events;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer fk_type;
	private Long timestamp;
	private Long fk_user_id;
	private Long fk_device_id;
	private Long fk_pet_id;
	private Long fk_zone_id;
	private Long fk_group_id;

	public Event() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getFk_type() {
		return fk_type;
	}

	public void setFk_type(Integer fk_type) {
		this.fk_type = fk_type;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getFk_user_id() {
		return fk_user_id;
	}

	public void setFk_user_id(Long fk_user_id) {
		this.fk_user_id = fk_user_id;
	}

	public Long getFk_device_id() {
		return fk_device_id;
	}

	public void setFk_device_id(Long fk_device_id) {
		this.fk_device_id = fk_device_id;
	}

	public Long getFk_pet_id() {
		return fk_pet_id;
	}

	public void setFk_pet_id(Long fk_pet_id) {
		this.fk_pet_id = fk_pet_id;
	}

	public Long getFk_zone_id() {
		return fk_zone_id;
	}

	public void setFk_zone_id(Long fk_zone_id) {
		this.fk_zone_id = fk_zone_id;
	}

	public Long getFk_group_id() {
		return fk_group_id;
	}

	public void setFk_group_id(Long fk_group_id) {
		this.fk_group_id = fk_group_id;
	}

	@Override
	public String toString() {
		return "Event{" + "id=" + id + ", type=" + fk_type + ", timestamp=" + timestamp + ", fk_user_id=" + fk_user_id
				+ ", fk_device_id=" + fk_device_id + ", fk_pet_id=" + fk_pet_id + ", fk_zone_id=" + fk_zone_id
				+ ", fk_group_id=" + fk_group_id + '}';
	}
}