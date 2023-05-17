package edu.ktu.pettrackerclient.zones.zone_points;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyPolygon {
    public List<LatLng> polygon_points;
    public List<MyLine> lines;
    public MyPolygon(List<LatLng> polygon_points) {
        this.polygon_points = new ArrayList<>();
        for (LatLng polygon_point : polygon_points) {
            LatLng temp = new LatLng(polygon_point.latitude, polygon_point.longitude);
            this.polygon_points.add(temp);
        }
        this.lines = new ArrayList<>();
        createLines();
    }
    public void createLines() {
        for(int i = 0; i < polygon_points.size(); i++) {
            if(i < polygon_points.size()-1) {
                lines.add(new MyLine(polygon_points.get(i), polygon_points.get(i+1)));
            } else {
                lines.add(new MyLine(polygon_points.get(i), polygon_points.get(0)));
            }
        }
    }
    public void addPoint(LatLng new_point) {
        LatLng first = polygon_points.get(0);
        LatLng old_last = polygon_points.get(polygon_points.size()-1);
        // new point is added to the end of array.
        polygon_points.add(new_point);
        // line old_last --> first is replaced by line old_last --> new_point
        // line new_point --> first appears
        lines.set(lines.size()-1, new MyLine(old_last, new_point));
        lines.add(new MyLine(new_point, first));
    }
}
