package edu.ktu.pettrackerclient.location_entries;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.zones.ZoneWithPoints;
import edu.ktu.pettrackerclient.zones.ZonesForDeviceResponse;
import edu.ktu.pettrackerclient.zones.zone_points.ZonePoint;


public class HistoryFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener  {

    TextView noEntries;
    SupportMapFragment mapFragment;
    GoogleMap map2;
    public HistoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        noEntries = v.findViewById(R.id.locationHistory_noData);
        locationHistory = new ArrayList<>();

        pet_stroke_color = ResourcesCompat.getColor(getResources(), R.color.emerald, null);
        pet_fill_color = (pet_stroke_color & 0x00FFFFFF) | 0xA0000000;

        group_stroke_color = ResourcesCompat.getColor(getResources(), R.color.gold, null);
        group_fill_color = (group_stroke_color & 0x00FFFFFF) | 0xA0000000;


        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map2);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map2, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return v;
    }

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    List<LocationEntry> locationHistory;
    private LocationEntryDelegation getHistory;
    public void setGetHistory(LocationEntryDelegation mth) {
        this.getHistory = mth;
    }
    public List<LocationEntry> executeGetHistory() { return getHistory.myMethod(); }

    ZonesForDeviceResponse zones;

    private ZonesListDelegation getZones;

    public void setGetZones(ZonesListDelegation mth) {
        this.getZones = mth;
    }

    public ZonesForDeviceResponse executeGetZones() { return getZones.myMethod(); }
    Polyline line;
    @Override
    public void onResume() {
        super.onResume();
        Log.d("1122", "history - on resume");

    }
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        Log.d("1122", "history - on pause");
    }

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.

                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) { }
    Polygon assignedToPet;
    List<Polygon> assignedToGroups;
    int pet_fill_color;
    int pet_stroke_color;

    int group_fill_color;
    int group_stroke_color;
    Marker last;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map2 = googleMap;
        zoomed = false;
        map2.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                Toast.makeText(getContext(), polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        handler.post(runnable = new Runnable() {
            public void run() {
                List<LocationEntry> list = executeGetHistory();
                locationHistory.clear();
                if(list != null && list.size() != 0) {
                    noEntries.setVisibility(View.INVISIBLE);
                    locationHistory.addAll(list);
                    if (line != null)
                        line.remove();
                    PolylineOptions opts = new PolylineOptions().clickable(false);
                    for(int i = 0; i < locationHistory.size(); i++) {
                        opts.add(new LatLng(locationHistory.get(i).getLatitude(), locationHistory.get(i).getLongitude()));
                    }

                    line = map2.addPolyline(opts);
                    line.setTag("B");
                    stylePolyline(line);
                    if(!zoomed) {
                        map2.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationHistory.get(0).getLatitude(), locationHistory.get(0).getLongitude()), 15));
                        zoomed = true;
                    }
                    if(last != null)
                        last.remove();
                    last = map2.addMarker(new MarkerOptions()
                            .position(new LatLng(locationHistory.get(0).getLatitude(), locationHistory.get(0).getLongitude()))
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.icon2)));

                } else {
                    noEntries.setVisibility(View.VISIBLE);
                    noEntries.bringToFront();
                    Log.d("1122", "last location - location list null or empty");
                }
                if(zones == null) {
                    zones();
                }

                handler.postDelayed(runnable, delay);

            }
        });
    }

    public void zones() {
        if(map2 == null)
            return;
        assignedToGroups = new ArrayList<>();
        zones = executeGetZones();

        if(zones != null && zones.assignedToPet != null)
        {
            Log.d("1122", "history - zones - pet" + zones.assignedToPet);
            Polygon temp = map2.addPolygon(new PolygonOptions()
                    .addAll(for_polygon(zones.assignedToPet.getPoints()))
                    .strokeColor(pet_stroke_color)
                    .fillColor(pet_fill_color)
                    .clickable(true));
            temp.setTag(labelForZone(zones.assignedToPet.getId()));
            assignedToPet = temp;
            Log.d("1122", "history - zones - pet polygon" + assignedToPet);

        }
        if(zones != null && zones.assignedToGroups != null) {
            Log.d("1122", "history - zones - groups" + zones.assignedToGroups);
            for(ZoneWithPoints z : zones.assignedToGroups) {
                if (zones.assignedToPet == null || !zones.assignedToPet.getId().equals(z.getId())) {
                    Polygon temp = map2.addPolygon(new PolygonOptions()
                            .addAll(for_polygon(z.getPoints()))
                            .strokeColor(group_stroke_color)
                            .fillColor(group_fill_color)
                            .clickable(true));
                    Log.d("1122", labelForZone(z.getId()));
                    temp.setTag(labelForZone(z.getId()));
                    assignedToGroups.add(temp);
                }

            }
        }
    }

    public List<LatLng> for_polygon(List<ZonePoint> points) {
        List<LatLng> list = new ArrayList<>();
        for(ZonePoint p : points) {
            list.add(new LatLng(p.getLatitude(), p.getLongitude()));
        }
        return list;
    }

    public String labelForZone(Long zone_id) {
        if(zones.assignedToPet != null && zones.assignedToPet.getId().equals(zone_id)) {
            return zones.assignedToPet.getZone_name() + " (assigned to pet)";
        } else {
            if(zones.assignedToGroups != null) {
                for(ZoneWithPoints z : zones.assignedToGroups) {
                    for(PetGroup gr : zones.groups) {
//                        if(gr.getFk_zone_id().equals(z.getId())) {
                        if(z.getId().equals(zone_id) && gr.getFk_zone_id().equals(z.getId())) {
                            return z.getZone_name() + " (assigned to group " + gr.getName() + ")";
                        }
                    }
                }
            }
        }
        return "";
    }
    private boolean zoomed;
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}