package edu.ktu.pettrackerclient;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.retrofit.LocationEntryApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LastLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LastLocationFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RetrofitService retro;
    LocationEntryApi location_api;
    SupportMapFragment mapFragment;
    Button b;
    LocationEntry latest;
    TextView noEntries;
    public LastLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LastLocationFragment newInstance(String param1, String param2) {
        LastLocationFragment fragment = new LastLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("1122", "im in on create view" );

        View v = inflater.inflate(R.layout.fragment_last_location, container, false);
        noEntries = v.findViewById(R.id.lastLocation_noData);
//        String device_id = this.getArguments().getString("device_id");
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long device_id = pref.getLong("device_id", 0);
        Log.d("1122", String.valueOf(device_id));

        Toast.makeText(getContext(), "device id is " + device_id, Toast.LENGTH_SHORT).show();
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        Log.d("1122", mapFragment.toString());
        retro = new RetrofitService();
        location_api = retro.getRetrofit().create(LocationEntryApi.class);
//        Handler handler = new Handler();
//        SocketThread thread = new SocketThread(location_api, handler);
//        thread.start();

        location_api.getLastForDevice(token, device_id)
                .enqueue(new Callback<LocationEntry>() {
                    @Override
                    public void onResponse(Call<LocationEntry> call, Response<LocationEntry> response) {
                        saveResult(response.body());
                        if(latest == null) {
                            noEntries.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            noEntries.setVisibility(View.INVISIBLE);
                        }
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latest.getLatitude(),latest.getLongitude()))
                                        .title("LinkedIn")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latest.getLatitude(),latest.getLongitude()), 10));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<LocationEntry> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

//        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment == null) {
//            FragmentManager fragmentManager = getParentFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            mapFragment = SupportMapFragment.newInstance();
//            fragmentTransaction.replace(R.id.map, mapFragment).commit();
//        }
//        mapFragment.getMapAsync(this);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        String mytag = "1122";
        location_api.getLastForDevice(token, 1L)
                .enqueue(new Callback<LocationEntry>() {
                    @Override
                    public void onResponse(Call<LocationEntry> call, Response<LocationEntry> response) {
//                        Log.d(mytag, "im in on resume for latest" + String.valueOf(response.body()));

                        saveResult(response.body());
                        if(latest == null) {
                            noEntries.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            noEntries.setVisibility(View.INVISIBLE);
                        }
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
//                                Log.d(mytag, "im in map ready in on resume for latest" );

                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latest.getLatitude(),latest.getLongitude()))
                                        .title("LinkedIn")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latest.getLatitude(),latest.getLongitude()), 10));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<LocationEntry> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveResult(LocationEntry entry) {
        this.latest = entry;
    }
    @Override
    public void onMapReady(GoogleMap map) {
//        Log.d("1122", "im in map ready outside of request" );

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(latest.getLattitude(),latest.getLongitude()))
//                .title("LinkedIn")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4233438, -122.0728817), 10));

    }
    class SocketThread extends Thread {
        private final LocationEntryApi api;
        private final Handler handler;
        SocketThread(LocationEntryApi api, Handler h) {
            this.api = api;
            this.handler = h;
        }
        @Override
        public void run() {
            // socket code goes here
            // whenever you receive a part that should update the ui,
            // do something like:
            while(true) {
                api.getLastForDevice("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZWciLCJpYXQiOjE2Nzc2MTAwNjUsImV4cCI6MTY3NzY5NjQ2NX0.iR_F5EuW76eEn_8NGujoeLUbdNecoA2hKst2Y40C8JoZlsqGy6-SytmQrXbSTtN2SHRc515_ZrKz7rGFiJg-zA", 1L)
                        .enqueue(new Callback<LocationEntry>() {
                            @Override
                            public void onResponse(Call<LocationEntry> call, Response<LocationEntry> response) {
                                saveResult(response.body());
                                if(mapFragment != null) {
                                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(latest.getLatitude(),latest.getLongitude()))
                                                    .title("LinkedIn")
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latest.getLatitude(),latest.getLongitude()), 10));
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onFailure(Call<LocationEntry> call, Throwable t) {
                                Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                try {
                    currentThread().sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    public GpsActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        this.activity = activity;
    }


}