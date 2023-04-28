package com.pettracker.pettrackerserver.model.location_entry.calculations;

public class MyLine {
    public LatLng p1, p2;
    public double a, b, c;
    public MyLine(LatLng p1, LatLng p2) {
        this.p1 = p1;
        this.p2 = p2;
        a = p2.longitude - p1.longitude;
        b = p1.latitude - p2.latitude;
        c = (p2.latitude * p1.longitude) - (p1.latitude * p2.longitude);
    }
    public double insertIntoEquation(LatLng p) {
        return a * p.latitude + b * p.longitude + c;
    }
}
