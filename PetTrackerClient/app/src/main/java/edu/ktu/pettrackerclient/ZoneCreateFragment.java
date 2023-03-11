package edu.ktu.pettrackerclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.model.MyLine;
import edu.ktu.pettrackerclient.model.MyPolygon;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import edu.ktu.pettrackerclient.retrofit.ZonePointApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoneCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneCreateFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SupportMapFragment mapFragment;
    private GoogleMap map;

    public ZoneCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZoneCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoneCreateFragment newInstance(String param1, String param2) {
        ZoneCreateFragment fragment = new ZoneCreateFragment();
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
    FloatingActionButton save;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_zone_create, container, false);
        save = v.findViewById(R.id.zoneCreate_Btn);
        RetrofitService retrofitService = new RetrofitService();
        ZonePointApi zonePointApi = retrofitService.getRetrofit().create(ZonePointApi.class);
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter a name for your zone");

                // Set up the input
                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Zone saveZone = new Zone();
                        saveZone.setName(input.getText().toString());
                        saveZone.setFk_user_id(pref.getLong("user_id", 0));
                        zoneApi.addZone(token, saveZone)
                                .enqueue(new Callback<Zone>() {
                                    @Override
                                    public void onResponse(Call<Zone> call, Response<Zone> response) {
                                        Log.d("1122", String.valueOf(response.body()));
                                        if(response.isSuccessful()) {
                                            List<ZonePoint> savePoints = convertForSave(response.body());
                                            Log.d("1122", "pre-req" + savePoints);
                                            zonePointApi.addZonePoints(token, savePoints)
                                                    .enqueue(new Callback<List<ZonePoint>>() {
                                                        @Override
                                                        public void onResponse(Call<List<ZonePoint>> call, Response<List<ZonePoint>> response) {
                                                            Log.d("1122", String.valueOf(response.body()));
                                                            Toast.makeText(getContext(),  "created zone successfully", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(view).navigate(R.id.action_drawerNav_createZone_to_drawerNav_zoneList);
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<ZonePoint>> call, Throwable t) {
                                                            Toast.makeText(getContext(), "Failed to load zones", Toast.LENGTH_LONG).show();
                                                            Log.d("1122", String.valueOf(t));

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Zone> call, Throwable t) {
                                        Toast.makeText(getContext(), "Failed to save zone", Toast.LENGTH_LONG).show();
                                        Log.d("1122", String.valueOf(t));
                                    }
                                });


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });

        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_zone);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_zone, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        saved_points = new ArrayList<>();
        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.setOnMapClickListener(this);
    }
    List<LatLng> saved_points;
    MyPolygon current_polygon;
    Polygon map_polygon;

    @Override
    public void onMapClick(LatLng point) {
        Log.d("1122", "clicked on lat:" + point.latitude +", lng:" + point.longitude);
        if(saved_points.size() < 3 ) {
            Log.d("1122", " if < 3");
            saved_points.add(point);
            if(saved_points.size() == 3) {
                map_polygon = map.addPolygon(new PolygonOptions()
                        .addAll(saved_points)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE));
                current_polygon = new MyPolygon(saved_points);
            }
        } else {
            MyPolygon new_polygon = new MyPolygon(saved_points);
            new_polygon.addPoint(point);
            if(canDraw(new_polygon)) {
                saved_points.add(point);
                current_polygon = new MyPolygon(saved_points);
                map_polygon.setPoints(saved_points);
            } else {
                Toast.makeText(getContext(), "can't draw, lines intersect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean canDraw(MyPolygon new_polygon) {
        boolean canDraw = true;
        for(int ii = 0; ii < new_polygon.lines.size(); ii++) {
            for(int jj = 0; jj < new_polygon.lines.size(); jj++) {
                if(ii!=jj && Math.abs(ii-jj) > 1 && Math.abs(ii-jj) != new_polygon.lines.size()-1){ // not the same line, not adjacent lines, not the first and last(they're also adjacent)
                    boolean intersect = doIntersect(new_polygon.lines.get(ii), new_polygon.lines.get(jj));
                    if(intersect) {
                        canDraw = false;
                    }
                }
            }
        }
        return canDraw;
    }
    public List<ZonePoint> convertForSave(Zone zone) {
        List<ZonePoint> result = new ArrayList<>();
        Log.d("1122", String.valueOf(saved_points.size()));
        for(int i = 0; i < saved_points.size(); i++) {
            ZonePoint pt = new ZonePoint();
            pt.setLatitude(saved_points.get(i).latitude);
            pt.setLongitude(saved_points.get(i).longitude);
            pt.setList_index(i);
            pt.setFk_zone_id(zone.getId());
            result.add(pt);
        }

        return result;
    }
    public boolean doIntersect(MyLine l1, MyLine l2) {
        MyLine il1 = new MyLine(l1.p1, l1.p2);
        if(il1.insertIntoEquation(l2.p1) > 0 == il1.insertIntoEquation(l2.p2) > 0) {
            //does not intersect
            return false;
        }
        MyLine il2 = new MyLine(l2.p1, l2.p2);
        if(il2.insertIntoEquation(l1.p1) > 0 == il2.insertIntoEquation(l1.p2) > 0) {
            //does not intersect
            return false;
        }
        if((il1.a * il2.b) - (il2.a * il1.b) == 0.0) {
            // collinear
            return false;
        }
        return true;
    }

}