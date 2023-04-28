package com.pettracker.pettrackerserver.model.pet_group;

import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class PetGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long fk_user_id;
	private Long fk_zone_id;
    private boolean notifications;

	public PetGroup() {
		
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

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fk_user_id=" + fk_user_id +
                ", fk_zone_id=" + fk_zone_id +
                '}';
    }
	
}