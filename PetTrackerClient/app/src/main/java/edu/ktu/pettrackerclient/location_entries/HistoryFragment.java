package edu.ktu.pettrackerclient.location_entries;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.zones.ZonesForDeviceResponse;


public class HistoryFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener  {

    TextView noEntries;
    SupportMapFragment mapFragment;
    GoogleMap map;
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
    int delay = 2000;
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = map;

        zones = executeGetZones();
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

                    line = googleMap.addPolyline(opts);
                    line.setTag("B");
                    stylePolyline(line);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationHistory.get(0).getLatitude(), locationHistory.get(0).getLongitude()), 15));

                } else {
                    noEntries.setVisibility(View.VISIBLE);
                    Log.d("1122", "last location - location list null or empty");
                }
                handler.postDelayed(runnable, delay);

            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}