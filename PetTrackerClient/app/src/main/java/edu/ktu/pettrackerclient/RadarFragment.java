package edu.ktu.pettrackerclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.retrofit.LocationEntryApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RadarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RetrofitService retro;
    LocationEntryApi location_api;
    LocationEntry latest;
    private SensorManager mSensorManager;
    private Sensor sens;
    private int counter;
    private ImageView img;
    Button btn;
    public RadarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RadarFragment newInstance(String param1, String param2) {
        RadarFragment fragment = new RadarFragment();
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
//        String device_id = this.getArguments().getString("device_id");

        View v = inflater.inflate(R.layout.fragment_radar, container, false);

        img = v.findViewById(R.id.imageDraw);
//        String device_id = this.getArguments().getString("device_id");
//        Toast.makeText(getContext(), "device id is " + device_id, Toast.LENGTH_SHORT).show();


        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sens = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        counter = 0;

        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
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

    private SensorEventListener mLightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(counter == 10) {
                counter =0;
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                FlingAnimation flingAnimation = new FlingAnimation(img, DynamicAnimation.ROTATION);
                flingAnimation.setFriction(0.3f);
                flingAnimation.setStartVelocity(100);
                flingAnimation.setStartValue(500);
                flingAnimation.setMinValue(Integer.MIN_VALUE);
                flingAnimation.setMaxValue(Integer.MAX_VALUE);
                flingAnimation.start();
                flingAnimation.start();
                Log.d("1122", String.valueOf("x" + Math.asin(x)));
                Log.d("1122", String.valueOf("y" + Math.asin(y)));
                Log.d("1122", String.valueOf("z" + Math.asin(z)));
                Log.d("1122", String.valueOf("cos" +event.values[3]));

            }
            else counter++;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("1122", sensor.toString() + " - " + accuracy);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (sens != null) {
            mSensorManager.registerListener(mLightSensorListener, sens,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
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