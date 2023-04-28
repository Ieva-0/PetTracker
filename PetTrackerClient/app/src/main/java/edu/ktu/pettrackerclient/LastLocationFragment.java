package edu.ktu.pettrackerclient;

import android.content.Context;
import android.os.Bundle;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.LocationEntryDelegation;
import edu.ktu.pettrackerclient.retrofit.ZonePointDelegation;


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

        View v = inflater.inflate(R.layout.fragment_last_location, container, false);
        noEntries = v.findViewById(R.id.lastLocation_noData);


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
    Marker reference;
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        handler.post(runnable = new Runnable() {
            public void run() {
                List<LocationEntry> list = executeGetEntry();
                if(list != null && list.size() != 0) {
                    noEntries.setVisibility(View.INVISIBLE);
                    latest = list.get(0);
                    if (reference != null)
                        reference.remove();
                    LatLng temp = new LatLng(latest.getLatitude(), latest.getLongitude());
                    reference = map.addMarker(new MarkerOptions().position(temp));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));

                } else {
                    noEntries.setVisibility(View.VISIBLE);
                    Log.d("1122", "last location - location list null or empty");
                }
                handler.postDelayed(runnable, delay);
            }
        });
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


}