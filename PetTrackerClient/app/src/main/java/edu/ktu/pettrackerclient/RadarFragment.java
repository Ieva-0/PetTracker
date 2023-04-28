package edu.ktu.pettrackerclient;

import static android.content.Context.LOCATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
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
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;

import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.retrofit.LocationEntryApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RadarFragment extends Fragment {
    RetrofitService retro;
    LocationEntryApi location_api;
    LocationEntry latest;
    private SensorManager mSensorManager;
    private Sensor sens;
    private int counter;
    private ImageView img;
    Button btn;
    private LocationEntry locationEntry;
    private LocationEntry currentLocation;

    public RadarFragment() {
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
//        String device_id = this.getArguments().getString("device_id");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        locationEntry = new LocationEntry();
        locationEntry.setLatitude(54.925647);
        locationEntry.setLongitude(23.968218);
        currentLocation = new LocationEntry();
        currentLocation.setLatitude(54.926707);
        currentLocation.setLongitude(23.938306);
        old_angle = 0f;
        View v = inflater.inflate(R.layout.fragment_radar, container, false);

        img = v.findViewById(R.id.imageDraw);
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
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        retro = new RetrofitService();
        location_api = retro.getRetrofit().create(LocationEntryApi.class);
        location_api.getLastForDevice(token, 1L)
                .enqueue(new Callback<LocationEntry>() {
                    @Override
                    public void onResponse(Call<LocationEntry> call, Response<LocationEntry> response) {
                        saveResult(response.body());

                    }

                    @Override
                    public void onFailure(Call<LocationEntry> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
        return v;
    }

    public MainActivity activity;

    public void saveResult(LocationEntry entry) {
        this.latest = entry;
    }

    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;

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
            if (time - last_updated > 1000) {
                last_updated = time;
                code();
            }

            if (false) {

                //point a - current
                //point b - entry
//                double a = locationEntry.getLongitude() - currentLocation.getLongitude();
//                double b = currentLocation.getLatitude() - locationEntry.getLatitude();
//                double c = currentLocation.getLongitude() * locationEntry.getLatitude() - currentLocation.getLatitude() * locationEntry.getLongitude();
//                double m = (Math.toDegrees(Math.atan((locationEntry.getLongitude() - currentLocation.getLongitude()) / (locationEntry.getLatitude() - currentLocation.getLatitude()))) + 360) % 360;
//                Log.d("1122", "m " + String.valueOf(m));

//                double a = locationEntry.getLongitude() - currentLocation.getLongitude();
//                double b = currentLocation.getLatitude() - locationEntry.getLatitude();
//                double c = currentLocation.getLongitude() * locationEntry.getLatitude() - currentLocation.getLatitude() * locationEntry.getLongitude();
//                double m = (Math.toDegrees(Math.atan2(locationEntry.getLongitude() - currentLocation.getLongitude(), locationEntry.getLatitude() - currentLocation.getLatitude())) + 360) % 360;
//                Log.d("1122", "m " + String.valueOf(m));

                double lat1 = currentLocation.getLatitude();
                double lon1 = currentLocation.getLongitude();
                double lat2 = locationEntry.getLatitude();
                double lon2 = locationEntry.getLongitude();
                double dLon = Math.toRadians(lon2 - lon1);
                double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));
                double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                        Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);
                double bearing = Math.toDegrees(Math.atan2(y, x));

                if (bearing < 0) {
                    bearing += 360;
                }

                counter = 0;
                double phonex = event.values[0];
                double phoney = event.values[1];
                double phonez = event.values[2];
                phonez = (Math.toDegrees(Math.asin(phonez)) + 360) % 360;
                Log.d("1122", "z " + String.valueOf(phonez));

                float new_angle = (float) bearing + (float) phonez;
                float shortestAngle = (float) (((((new_angle - old_angle) % 360) + 540) % 360) - 180);

                // Create an ObjectAnimator that will rotate the view
                ObjectAnimator animator = ObjectAnimator.ofFloat(img, "rotation", old_angle, shortestAngle + old_angle);
// Set the duration and interpolator of the animation
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());

// Set the repeat count of the animation to infinite
                animator.setRepeatCount(0);
// Set the repeat mode of the animation to reverse
//                animator.setRepeatMode(ValueAnimator.REVERSE);

// Start the animation
                animator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        img.setRotation(new_angle);
                        old_angle = new_angle;
                        Log.d("1122", "old angle:" + String.valueOf(new_angle));
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {
                    }
                });
                animator.start();

//                FlingAnimation flingAnimation = new FlingAnimation(img, DynamicAnimation.ROTATION);
//                flingAnimation.setFriction(0.1f);
//                flingAnimation.setStartVelocity(-100);
////                flingAnimation.setStartValue((float) z);
//                float minVal = (float) (Math.min(z, m));
//                float maxVal = (float) (Math.max(z, m));
//                flingAnimation.setMinValue((float) minVal);
//                flingAnimation.setMaxValue((float) maxVal );
//                flingAnimation.start();

//                Log.d("1122", String.valueOf("x " + Math.toDegrees(Math.asin(x))));
//                Log.d("1122", String.valueOf("y " + Math.toDegrees(Math.asin(y))));
//                Log.d("1122", String.valueOf("z " + Math.toDegrees(Math.asin(z))));
//                Log.d("1122", String.valueOf("cos " + event.values[3]));
// Get the view to be animated


            } else counter++;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    float[] accelerometerValues;
    float[] magneticValues;
    float[] rotationValues;
    public void code() {
//        Log.d("1122", "-----------------------radar");

        float[] rotationMatrix = new float[9];
        float[] orientationAngles = new float[3];
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

//        if (success) {
//            Log.d("1122", "accel ");
//            Log.d("1122", Arrays.toString(accelerometerValues));
//            Log.d("1122", "magnet ");
//            Log.d("1122", Arrays.toString(magneticValues));
////        Log.d("1122", "rotation ");
////        Log.d("1122", Arrays.toString(rotationMatrix));
//            Log.d("1122", "orientation ");
//            Log.d("1122", String.valueOf(orientationAngles));
//        }

// Calculate the bearing between your location and the target point
        double lat1 = currentLocation.getLatitude();
        double lon1 = currentLocation.getLongitude();
        double lat2 = locationEntry.getLatitude();
        double lon2 = locationEntry.getLongitude();
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
//        Log.d("1122", "short angle " + shortestAngle);

// Create an ObjectAnimator to rotate the compass needle or arrow to the calculated angle
        ObjectAnimator animator = ObjectAnimator.ofFloat(img, "rotation", img.getRotation(), rotationAngle);

// Set the duration and interpolator of the animation
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.start();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (sens != null) {
//            mSensorManager.registerListener(mLightSensorListener, sens,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
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