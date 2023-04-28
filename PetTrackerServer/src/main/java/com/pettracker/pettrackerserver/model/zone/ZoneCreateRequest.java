package com.pettracker.pettrackerserver.model.zone;

import java.util.List;

import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;

public class ZoneCreateRequest {
    private String zone_name;
    private List<ZonePoint> points;

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
   
}