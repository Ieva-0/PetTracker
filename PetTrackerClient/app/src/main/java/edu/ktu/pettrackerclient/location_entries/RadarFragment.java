package edu.ktu.pettrackerclient.location_entries;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import edu.ktu.pettrackerclient.MainActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RadarFragment extends Fragment {
    LocationEntry latest;
    private SensorManager mSensorManager;
    private Sensor sens;
    private int counter;
    private ImageView img;
    private LocationEntry locationEntry;
    private LocationEntry currentLocation;
    private LocationEntryDelegation getEntry;
    public void setGetEntry(LocationEntryDelegation mth) {
        this.getEntry = mth;
    }
    public List<LocationEntry> executeGetEntry() { return getEntry.myMethod(); }
    public RadarFragment() {
        // Required empty public constructor
    }

    TextView noEntries;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        String device_id = this.getArguments().getString("device_id");

        View v = inflater.inflate(R.layout.fragment_radar, container, false);

        noEntries = v.findViewById(R.id.radar_noData);

        img = v.findViewById(R.id.imageDraw);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        handler.post(runnable = new Runnable() {
            public void run() {
                List<LocationEntry> list = executeGetEntry();
                if(list != null && list.size() != 0) {
                    noEntries.setVisibility(View.INVISIBLE);
                    locationEntry = list.get(0);
                } else {
                    noEntries.setVisibility(View.VISIBLE);
                    Log.d("1122", "last location - location list null or empty");
                }

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            currentLocation = new LocationEntry();
                            currentLocation.setLatitude(location.getLatitude());
                            currentLocation.setLongitude(location.getLongitude());
                            Log.d("1122", "last known location " + location.toString());
                        }
                    });


                } else {
                    Log.d("1122", "last known location no pems ");

                }

                handler.postDelayed(runnable, delay);
            }
        });

//        locationEntry = new LocationEntry();
//        locationEntry.setLatitude(54.925647);
//        locationEntry.setLongitude(23.968218);
//        currentLocation = new LocationEntry();
//        currentLocation.setLatitude(54.926707);
//        currentLocation.setLongitude(23.938306);

        old_angle = 0f;


        img.bringToFront();
        accelerometerValues = new float[3];
        magneticValues = new float[3];
        rotationValues = new float[3];
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sens = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        counter = 0;
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(mLightSensorListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(mLightSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(mLightSensorListener, sens, SensorManager.SENSOR_DELAY_UI);

        return v;
    }

    public MainActivity activity;

    public void saveResult(LocationEntry entry) {
        this.latest = entry;
    }


    float old_angle;
    private SensorEventListener mLightSensorListener = new SensorEventListener() {

        long last_updated = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            long time = System.currentTimeMillis();

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerValues = event.values.clone();
//                    Log.d("1122", "values - acc ");

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticValues = event.values.clone();
//                    Log.d("1122", "values - magn ");

                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    rotationValues = event.values.clone();
//                    Log.d("1122", "values - rot ");

                default:
                    return;
            }
//            Log.d("1122", Arrays.toString(event.values));
            if (time - last_updated > 1000 && locationEntry != null && currentLocation != null) {
                noEntries.setVisibility(View.GONE);
                last_updated = time;
                code();
            }
            if(locationEntry == null || currentLocation == null) {
                noEntries.setVisibility(View.VISIBLE);

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    float[] accelerometerValues;
    float[] magneticValues;
    float[] rotationValues;
    public void code() {

        float[] rotationMatrix = new float[9];
        float[] orientationAngles = new float[3];
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);


        double lat2 = currentLocation.getLatitude();
        double lon2 = currentLocation.getLongitude();
        double lat1 = locationEntry.getLatitude();
        double lon1 = locationEntry.getLongitude();
        double dLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y, x));

        if (bearing < 0) {
            bearing += 360;
        }

// Calculate the rotation angle for the compass needle or arrow
        float phoneRotation = (float) Math.toDegrees(orientationAngles[0]);
        float rotationAngle = (float) (bearing - phoneRotation);
//        Log.d("1122", "haversine bearing " + bearing);
//        Log.d("1122", "phonerotation " + phoneRotation);
//        Log.d("1122", "final angle " + rotationAngle);
        float shortestAngle = (float) ((((rotationAngle % 360) + 540) % 360) - 180);

        ObjectAnimator animator = ObjectAnimator.ofFloat(img, "rotation", img.getRotation(), rotationAngle);

        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sens != null) {
            mSensorManager.unregisterListener(mLightSensorListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = activity;
    }
}