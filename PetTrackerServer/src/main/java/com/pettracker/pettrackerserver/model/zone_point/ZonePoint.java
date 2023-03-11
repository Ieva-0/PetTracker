package com.pettracker.pettrackerserver.model.zone_point;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.pettracker.pettrackerserver.model.zone.Zone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
public class ZonePoint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double latitude;
	private double longitude;
	private int list_index;
	private Long fk_zone_id;
//	@ManyToOne
//    @JoinColumn(name = "fk_zone_id", referencedColumnName = "id")
//	private Zone zone;
	
	public double getLatitude() {
		return latitude;
	}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
	public int getList_index() {
		return list_index;
	}
	public void setList_index(int list_index) {
		this.list_index = list_index;
	}
	public Long getFk_zone_id() {
		return fk_zone_id;
	}
	public void setFk_zone_id(Long fk_zone_id) {
		this.fk_zone_id = fk_zone_id;
	}
    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", list_index=" + list_index +
                ", fk_zone_id=" + fk_zone_id +
                '}';
    }
}