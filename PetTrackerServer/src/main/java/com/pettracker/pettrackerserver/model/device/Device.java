package com.pettracker.pettrackerserver.model.device;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class Device {
	
	@Id
	private String id;
	private String name;
	private int user_id_foreign;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id_foreign() {
        return user_id_foreign;
    }

    public void setUser_id_foreign(int user_id_foreign) {
        this.user_id_foreign = user_id_foreign;
    }
    
    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_id_foreign=" + user_id_foreign +
                '}';
    }
	
	
}