package edu.ktu.pettrackerclient.zones.zone_points;

public class ZonePoint {
    public Long id;
    public double latitude;
    public double longitude;
    public int list_index;
    public Long fk_zone_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFk_zone_id() {
        return fk_zone_id;
    }

    public void setFk_zone_id(Long fk_zone_id) {
        this.fk_zone_id = fk_zone_id;
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

    public int getList_index() {
        return list_index;
    }

    public void setList_index(int list_index) {
        this.list_index = list_index;
    }


}
