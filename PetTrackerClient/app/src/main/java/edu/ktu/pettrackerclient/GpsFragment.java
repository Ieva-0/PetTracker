package edu.ktu.pettrackerclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GpsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GpsFragment extends Fragment implements NavigationBarView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GpsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GpsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GpsFragment newInstance(String param1, String param2) {
        GpsFragment fragment = new GpsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    String device_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gps, container, false);
        device_id = getArguments().getString("device_id");
        bottomNavigationView = v.findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_lastLocation);

        return v;
    }
    LastLocationFragment lastLocationFragment = new LastLocationFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    RadarFragment radarFragment = new RadarFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("device_id", device_id);
        switch (item.getItemId()) {
            case R.id.bottomNavigation_lastLocation:
                lastLocationFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.gpsFragment_fragmentContainer, lastLocationFragment).commit();
                return true;

            case R.id.bottomNavigation_history:
                historyFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.gpsFragment_fragmentContainer, historyFragment).commit();
                return true;

            case R.id.bottomNavigation_radar:
                radarFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.gpsFragment_fragmentContainer, radarFragment).commit();
                return true;
        }
        return false;
    }
    public test activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = activity;
    }
}