package edu.ktu.pettrackerclient.location_entries;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.zones.zone_points.ZonePoint;
import edu.ktu.pettrackerclient.zones.ZoneWithPoints;
import edu.ktu.pettrackerclient.zones.ZonesForDeviceResponse;


public class LastLocationFragment extends Fragment implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    GoogleMap map;
    Button b;
    LocationEntry latest;
    TextView noEntries;
    public LastLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        FragmentLastLocationBinding binding = FragmentLastLocationBinding.inflate(getLayoutInflater());

        View v = inflater.inflate(R.layout.fragment_last_location, container, false);
        noEntries = v.findViewById(R.id.lastLocation_noData);

        pet_stroke_color = ResourcesCompat.getColor(getResources(), R.color.emerald, null);
        pet_fill_color = (pet_stroke_color & 0x00FFFFFF) | 0xA0000000;

        group_stroke_color = ResourcesCompat.getColor(getResources(), R.color.gold, null);
        group_fill_color = (group_stroke_color & 0x00FFFFFF) | 0xA0000000;

        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return v;
    }
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;
    LocationEntry latestLocation;
    private LocationEntryDelegation getEntry;
    public void setGetEntry(LocationEntryDelegation mth) {
        this.getEntry = mth;
    }
    public List<LocationEntry> executeGetEntry() { return getEntry.myMethod(); }

    ZonesForDeviceResponse zones;

    private ZonesListDelegation getZones;

    public void setGetZones(ZonesListDelegation mth) {
        this.getZones = mth;
    }

    public ZonesForDeviceResponse executeGetZones() { return getZones.myMethod(); }
    Marker reference;

    Polygon assignedToPet;
    List<Polygon> assignedToGroups;
    int pet_fill_color;
    int pet_stroke_color;

    int group_fill_color;
    int group_stroke_color;
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        zoomed = false;
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                Toast.makeText(getContext(), polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        handler.post(runnable = new Runnable() {
            public void run() {
                List<LocationEntry> list = executeGetEntry();
                if(list != null && list.size() != 0) {
                    noEntries.setVisibility(View.INVISIBLE);
                    latest = list.get(0);
                    if (reference != null)
                        reference.remove();
                    LatLng temp = new LatLng(latest.getLatitude(), latest.getLongitude());
                    reference = map.addMarker(new MarkerOptions()
                            .position(temp)
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.icon2)));

                    if(!zoomed) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
                        zoomed = true;
                    }

                } else {
                    noEntries.setVisibility(View.VISIBLE);
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
        if(map == null)
            return;
        assignedToGroups = new ArrayList<>();
        zones = executeGetZones();

        if(zones != null && zones.assignedToPet != null)
        {
            Polygon temp = map.addPolygon(new PolygonOptions()
                    .addAll(for_polygon(zones.assignedToPet.getPoints()))
                    .strokeColor(pet_stroke_color)
                    .fillColor(pet_fill_color)
                    .clickable(true));
            temp.setTag(labelForZone(zones.assignedToPet.getId()));
            assignedToPet = temp;
        }
        if(zones != null && zones.assignedToGroups != null) {
            for(ZoneWithPoints z : zones.assignedToGroups) {
                if (zones.assignedToPet == null || !zones.assignedToPet.getId().equals(z.getId())) {
                    Polygon temp = map.addPolygon(new PolygonOptions()
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
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        Log.d("1122", "last location - on pause");
    }

    public void onAttach(Context context) {
        super.onAttach(context);
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
//    public Bitmap icon(int index) {
//
//
////        Resources res = getResources();
////        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.baseline_pets_24);
////        Canvas canvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true));
////        Paint paintbg = new Paint();
////        paintbg.setColor(getResources().getColor(R.color.gold));
////        paintbg.setStrokeWidth(50);
////        canvas.drawCircle(32, 32, 32, paintbg);
//
////        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
////        Bitmap bmp = Bitmap.createBitmap(64, 64, conf);
////        Paint paintbg = new Paint();
////        paintbg.setColor(getResources().getColor(R.color.gold));
////        paintbg.setStrokeWidth(50);
////        Canvas canvas = new Canvas(bmp);
////        canvas.drawCircle(32, 32, 32, paintbg);
////        Paint paintext = new Paint();
////        paintext.setColor(getResources().getColor(R.color.rich_black));
////        Resources res = getResources();
////        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.baseline_pets_24);
////        canvas.drawBitmap(bitmap, 0, 0, paintext);
////        canvas.drawText(String.valueOf(index), 15, 55, paintext); // paint defines the text color, stroke width, size
//        return bmp;
//    }
}