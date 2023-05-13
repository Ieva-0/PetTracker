package edu.ktu.pettrackerclient.zones;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.location_entries.MyLine;
import edu.ktu.pettrackerclient.location_entries.MyPolygon;
import edu.ktu.pettrackerclient.zones.zone_points.BottomSheet;
import edu.ktu.pettrackerclient.zones.zone_points.ZonePoint;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZoneCreateFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener, GoogleMap.OnMarkerDragListener {

    SupportMapFragment mapFragment;
    private GoogleMap map;
    FloatingActionButton save;
    FloatingActionButton openbottom;
    Long zone_id;
    ZoneWithPoints old_zone;
    public ZoneCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_zone_create, container, false);
        int color = ResourcesCompat.getColor(getResources(), R.color.emerald, null);
        fill_color = (color & 0x00FFFFFF) | 0xA0000000;
        stroke_color = ResourcesCompat.getColor(getResources(), R.color.gold, null);
        marker_references = new ArrayList<Marker>();

        save = v.findViewById(R.id.zoneCreate_Btn);
        openbottom = v.findViewById(R.id.modifyPoints_btn);
        openbottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ZonePoint> points = convertForSave(new Zone());
                Log.d("1122", points.toString());
                BottomSheet bottomSheet = new BottomSheet(points);
                bottomSheet.setRemove(new ZonePointDelegation() {
                    @Override
                    public void myMethod(int index) {
                        List<LatLng> points = updatePoints();
                        points.remove(index);
                        if (canDraw(points)) {
                            marker_references.get(index).remove();
                            marker_references.remove(index);
                            saved_points = updatePoints();
                            if (saved_points.size() > 2) {
                                map_polygon.setPoints(saved_points);
                            } else if(map_polygon!=null) {
                                map_polygon.remove();
                            }
                        } else {
                            Toast.makeText(getContext(), "Cannot remove this marker. Zone borders can't intersect.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                bottomSheet.setUpdate(new ZonePointDelegation() {
                    @Override
                    public void myMethod(int index) {
                        for (int i = 0; i < marker_references.size(); i++) {
                            marker_references.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(icon(i + 1)));
                        }
                        saved_points = updatePoints();
                        if (saved_points.size() >= 3) {
                            map_polygon.setPoints(saved_points);
                        } else {
                            if (map_polygon != null) {
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
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saved_points.size() < 3) {
                    Toast.makeText(getContext(), "Not enough points to create a zone, please choose at least three points.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Enter a name for your zone");
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = (View) inflater.inflate(R.layout.zone_name_dialog, null);

                TextInputLayout input = dialogView.findViewById(R.id.zoneCreate_name);
                if(old_zone != null)
                    input.getEditText().setText(old_zone.getZone_name());
                input.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (input.getEditText().getText().length() < 4 || input.getEditText().getText().length() > 20)
                            input.setError(getResources().getString(R.string.name_requirements));
                        else input.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                builder.setView(dialogView);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(input.getEditText().getText().toString().isEmpty()) {
                            input.setError(getResources().getString(R.string.name_requirements));
                            Toast.makeText(getContext(), "Please enter a name for the zone.", Toast.LENGTH_SHORT).show();
                        } else {
                            ZoneWithPoints req = new ZoneWithPoints();
                            Zone zone = new Zone();
                            if (old_zone != null && zone_id != null) {
                                req.setId(old_zone.getId());
                                zone.setId(old_zone.getId());
                            }
                            req.setZone_name(input.getEditText().getText().toString());
                            zone.setName(input.getEditText().getText().toString());
                            req.setUser_id(pref.getLong("user_id", 0));
                            zone.setFk_user_id(pref.getLong("user_id", 0));

                            List<ZonePoint> savePoints = convertForSave(zone);
                            req.setPoints(savePoints);

                            zoneApi.addZone(token, req)
                                    .enqueue(new Callback<MessageResponse>() {
                                        @Override
                                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                            if (response.isSuccessful()) {
                                                if(response.body().isSuccessful()) {
                                                    dialog.cancel();
                                                    Navigation.findNavController(view).navigate(R.id.action_drawerNav_createZone_to_drawerNav_zoneList);
                                                }
                                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            Log.d("1122", String.valueOf(t));
                                        }
                                    });
                        }

                    }
                });

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


        RetrofitService retrofitService = new RetrofitService();
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        try {
            zone_id = getArguments().getLong("zone_id");
        } catch (Exception e) {
            Log.d("1122", "no zone id");
        }

        if (zone_id != null) {
            ZoneApi zoneapi = retrofitService.getRetrofit().create(ZoneApi.class);
            zoneapi.getZoneWithPoints(token, zone_id).enqueue(new Callback<ZoneWithPoints>() {
                @Override
                public void onResponse(Call<ZoneWithPoints> call, Response<ZoneWithPoints> response) {
                    if (response.isSuccessful()) {
                        Log.d("1122", response.body().toString());
                        old_zone = response.body();
                        old_zone.getPoints().forEach(point -> {
                            LatLng p = new LatLng(point.getLatitude(), point.getLongitude());
                            marker_references.add(map.addMarker(
                                    new MarkerOptions()
                                            .position(p)
                                            .icon(BitmapDescriptorFactory.fromBitmap(icon(marker_references.size() + 1)))
                                            .draggable(true)));
                            saved_points.add(p);
                        });
                        Log.d("1122", String.valueOf(saved_points));
                        if (saved_points.size() >= 3) {
                            map_polygon = map.addPolygon(new PolygonOptions()
                                    .addAll(saved_points)
                                    .strokeColor(stroke_color)
                                    .fillColor(fill_color));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ZoneWithPoints> call, Throwable t) {

                }
            });
        }
    }

    List<LatLng> saved_points;
    MyPolygon current_polygon;
    Polygon map_polygon;
    List<Marker> marker_references;

    public void printMarkers() {
        if (marker_references != null) {
            for (Marker m : marker_references) {
                Log.d("1122", "| " + m.getPosition().latitude + ", " + m.getPosition().longitude + " | ");
            }
        }
    }

    int fill_color;
    int stroke_color;
    @Override
    public void onMapClick(LatLng point) {
        Log.d("1122", "clicked on lat:" + point.latitude + ", lng:" + point.longitude);
        Bitmap bmp = icon(marker_references.size() + 1);
        if (marker_references.size() < 3) {
            marker_references.add(map.addMarker(
                    new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                            .draggable(true)));
            saved_points = updatePoints();
            if (marker_references.size() == 3) {

                map_polygon = map.addPolygon(new PolygonOptions()
                        .addAll(saved_points)
                        .strokeColor(stroke_color)
                        .fillColor(fill_color));
            }
        } else {
            List<LatLng> current_points = updatePoints();
            current_points.add(point);
            if (canDraw(current_points)) {
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
        for (Marker m : marker_references) {
            points.add(m.getPosition());
        }
        return points;
    }

    public boolean canDraw(List<LatLng> points) {
        MyPolygon new_polygon = new MyPolygon(points);
        boolean canDraw = true;
        for (int ii = 0; ii < new_polygon.lines.size(); ii++) {
            for (int jj = 0; jj < new_polygon.lines.size(); jj++) {
                if (ii != jj && Math.abs(ii - jj) > 1 && Math.abs(ii - jj) != new_polygon.lines.size() - 1) { // not the same line, not adjacent lines, not the first and last(they're also adjacent)
                    boolean intersect = doIntersect(new_polygon.lines.get(ii), new_polygon.lines.get(jj));
                    if (intersect) {
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
        for (int i = 0; i < saved_points.size(); i++) {
            ZonePoint pt = new ZonePoint();
            pt.setLatitude(saved_points.get(i).latitude);
            pt.setLongitude(saved_points.get(i).longitude);
            pt.setList_index(i);
            if (zone.getId() != null)
                pt.setFk_zone_id(zone.getId());
            result.add(pt);
        }

        return result;
    }

    public boolean doIntersect(MyLine l1, MyLine l2) {
        MyLine il1 = new MyLine(l1.p1, l1.p2);
        if (il1.insertIntoEquation(l2.p1) >= 0 == il1.insertIntoEquation(l2.p2) >= 0) {
            Log.d("1122", "first if " + il1.insertIntoEquation(l2.p1) + " " + il1.insertIntoEquation(l2.p2));
            //does not intersect
            return false;
        }
        MyLine il2 = new MyLine(l2.p1, l2.p2);
        if (il2.insertIntoEquation(l1.p1) >= 0 == il2.insertIntoEquation(l1.p2) >= 0) {
            Log.d("1122", "second if " + il2.insertIntoEquation(l1.p1) + " " + il1.insertIntoEquation(l1.p2));
            //does not intersect
            return false;
        }
        if ((il1.a * il2.b) - (il2.a * il1.b) == 0.0) {
            // collinear
            return false;
        }
        return true;
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Log.d("1122", "dragged end");
        List<LatLng> newPoints = updatePoints();
        if (canDraw(newPoints)) {
            saved_points = updatePoints();
            map_polygon.setPoints(saved_points);
        } else {
            Toast.makeText(getContext(), "cannot move marker there. zone borders can't intersect.", Toast.LENGTH_SHORT).show();
            restoreMarkers();
        }
        printMarkers();
    }

    public void restoreMarkers() {
        for (int i = 0; i < saved_points.size(); i++) {
            marker_references.get(i).setPosition(saved_points.get(i));
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }
}