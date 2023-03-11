package com.pettracker.pettrackerserver.model.zone;

import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class Zone {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long fk_user_id;
	
//	@OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<ZonePoint> zonePoints = new HashSet<>();
//	
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
	
}