package com.pettracker.pettrackerserver.model.pet_group;

import javax.persistence.Id;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class PetToGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long pet_id;
	private Long group_id;

	public PetToGroup() {
		
	}
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", pet_id=" + pet_id +
                ", group_id=" + group_id +
                '}';
    }

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public Long getPet_id() {
		return pet_id;
	}

	public void setPet_id(Long pet_id) {
		this.pet_id = pet_id;
	}
	
}