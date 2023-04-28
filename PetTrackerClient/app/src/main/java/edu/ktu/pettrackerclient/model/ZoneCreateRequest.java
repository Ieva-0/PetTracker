package edu.ktu.pettrackerclient.model;

import java.util.List;

public class ZoneCreateRequest {
    private Long id;
    private String zone_name;
    private List<ZonePoint> points;
    private Long user_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getZone_name() {
        return zone_name;
    }
    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public List<ZonePoint> getPoints() {
        return points;
    }

    public void setPoints(List<ZonePoint> points) {
        this.points = points;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
