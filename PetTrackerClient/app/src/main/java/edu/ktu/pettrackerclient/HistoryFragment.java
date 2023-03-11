package edu.ktu.pettrackerclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.retrofit.LocationEntryApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView img;
    Button btn;
    private List<LocationEntry> locations;
    SupportMapFragment mapFragment;
    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View v = inflater.inflate(R.layout.fragment_history, container, false);
//        String device_id = this.getArguments().getString("device_id");
//        Toast.makeText(getContext(), "device id is " + this.getArguments().getString("device_id"), Toast.LENGTH_SHORT).show();
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map2);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map2, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        RetrofitService retrofitService = new RetrofitService();
        LocationEntryApi locationEntryApi = retrofitService.getRetrofit().create(LocationEntryApi.class);

        String mytag = "1122";
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        locationEntryApi.getHistoryForDevice(token, 1L)
                .enqueue(new Callback<List<LocationEntry>>() {
                    @Override
                    public void onResponse(Call<List<LocationEntry>> call, Response<List<LocationEntry>> response) {
                        saveResult(response.body());
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                drawing(googleMap);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(locations.size()-1).getLatitude(),locations.get(locations.size()-1).getLongitude()), 10));

                            }
                        });
                    }
                    @Override
                    public void onFailure(Call<List<LocationEntry>> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });


        return v;
    }

    public void saveResult(List<LocationEntry> res) {
        this.locations = res;
    }

    @Override
    public void onResume() {
        super.onResume();
        RetrofitService retrofitService = new RetrofitService();
        LocationEntryApi locationEntryApi = retrofitService.getRetrofit().create(LocationEntryApi.class);

        String mytag = "1122";
        Log.d(mytag, "im in on resume for history");
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        locationEntryApi.getHistoryForDevice(token, 1L)
                .enqueue(new Callback<List<LocationEntry>>() {
                    @Override
                    public void onResponse(Call<List<LocationEntry>> call, Response<List<LocationEntry>> response) {
                        saveResult(response.body());
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                drawing(googleMap);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<LocationEntry>> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void drawing(GoogleMap googleMap) {
        PolylineOptions opts = new PolylineOptions().clickable(false);
        for(int i = 0; i < locations.size(); i++) {
            opts.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
        }
        Polyline line = googleMap.addPolyline(opts);
        line.setTag("B");
        stylePolyline(line);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()), 4));

    }
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
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
    public void onPolygonClick(@NonNull Polygon polygon) {

    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
    public MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = activity;
    }
}