package edu.ktu.pettrackerclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.adapter.ZonePointAdapter;
import edu.ktu.pettrackerclient.model.MyLine;
import edu.ktu.pettrackerclient.model.MyPolygon;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.MyMethod;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import edu.ktu.pettrackerclient.retrofit.ZonePointApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZoneCreateFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener, GoogleMap.OnMarkerDragListener {

    SupportMapFragment mapFragment;
    private GoogleMap map;

    public ZoneCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FloatingActionButton save;
    FloatingActionButton openbottom;

    boolean editting;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_zone_create, container, false);

        marker_references = new ArrayList<Marker>();

        save = v.findViewById(R.id.zoneCreate_Btn);
        openbottom = v.findViewById(R.id.modifyPoints_btn);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d("1122", "hi there im back");
            }
        });
        openbottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
//                        saved_points.remove(saved_points.size()-1);
                        List<ZonePoint> points = convertForSave(new Zone());
                        Log.d("1122", points.toString());
                        BottomSheet bottomSheet = new BottomSheet(points);
                        bottomSheet.setRemove(new MyMethod() {
                            @Override
                            public void myMethod(int index) {
                                List<LatLng> points = updatePoints();
                                points.remove(index);
                                if(canDraw(points)) {
                                    marker_references.get(index).remove();
                                    marker_references.remove(index);
                                    saved_points = updatePoints();
                                    if(saved_points.size() != 0) {
                                        map_polygon.setPoints(saved_points);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "cannot remove this marker. zone borders can't intersect.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                        bottomSheet.setUpdate(new MyMethod() {
                            @Override
                            public void myMethod(int index) {
                                for(int i = 0; i < marker_references.size(); i++) {
                                    marker_references.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(icon(i+1)));
                                }
                                saved_points = updatePoints();
                                if(saved_points.size() >= 3) {
                                    map_polygon.setPoints(saved_points);
                                } else {
                                    if(map_polygon != null) {
                                        map_polygon.remove();
                                    }
                                }
                            }
                        });
                        bottomSheet.show(getActivity().getSupportFragmentManager(),
                                "ModalBottomSheet");
                    }
                });

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
        this.map.setOnMarkerDragListener(this);
    }
    List<LatLng> saved_points;
    MyPolygon current_polygon;
    Polygon map_polygon;

    List<Marker> marker_references;

    public void printMarkers() {
        if(marker_references != null) {
            for(Marker m : marker_references) {
                Log.d("1122", "| " + m.getPosition().latitude + ", " + m.getPosition().longitude + " | ");
            }
        }
    }
    @Override
    public void onMapClick(LatLng point) {
        Log.d("1122", "clicked on lat:" + point.latitude +", lng:" + point.longitude);
        Bitmap bmp = icon(marker_references.size()+1);
        if(marker_references.size() < 3 ) {
            marker_references.add(map.addMarker(
                    new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                            .draggable(true)));
            saved_points = updatePoints();
            if(marker_references.size() == 3) {
                map_polygon = map.addPolygon(new PolygonOptions()
                        .addAll(saved_points)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE));
            }
        } else {
            List<LatLng> current_points = updatePoints();
            current_points.add(point);
            if(canDraw(current_points)) {
                marker_references.add(map.addMarker(
                        new MarkerOptions()
                                .position(point)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                .draggable(true)));
                saved_points = updatePoints();
                map_polygon.setPoints(saved_points);
            } else {
                Toast.makeText(getContext(), "can't draw, lines intersect", Toast.LENGTH_SHORT).show();
            }
        }
        printMarkers();

    }

    public Bitmap icon(int index) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(50, 50, conf);
        Paint paintbg = new Paint();
        paintbg.setColor(Color.BLUE);
        paintbg.setStrokeWidth(50);
        Canvas canvas = new Canvas(bmp);
        canvas.drawCircle(25, 25, 25, paintbg);
        Paint paintext = new Paint();
        paintext.setColor(Color.GREEN);
        paintext.setStrokeWidth(50);
        paintext.setTextSize(65);
        canvas.drawText(String.valueOf(index), 0, 50, paintext); // paint defines the text color, stroke width, size
        return bmp;
    }

    public List<LatLng> updatePoints() {
        List<LatLng> points = new ArrayList<LatLng>();
        for(Marker m : marker_references) {
            points.add(m.getPosition());
        }
        return points;
    }
    public boolean canDraw(List<LatLng> points) {
        MyPolygon new_polygon = new MyPolygon(points);
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
            if(zone.getId() != null)
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

    @Override
    public void onMarkerDrag(@NonNull Marker marker) { }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Log.d("1122", "dragged end");
        List<LatLng> newPoints = updatePoints();
        if(canDraw(newPoints)) {
            saved_points = updatePoints();
            map_polygon.setPoints(saved_points);
        } else {
            Toast.makeText(getContext(), "cannot move marker there. zone borders can't intersect.", Toast.LENGTH_SHORT).show();
            restoreMarkers();
        }
        printMarkers();
    }

    public void restoreMarkers() {
        for(int i= 0; i < saved_points.size(); i++) {
            marker_references.get(i).setPosition(saved_points.get(i));
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) { }
}