package edu.ktu.pettrackerclient.location_entries;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.zones.ZonesForDeviceResponse;
import edu.ktu.pettrackerclient.RetrofitService;
import edu.ktu.pettrackerclient.zones.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    ImageButton lastUpdated;

    TextView lastUpdatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        locationsList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_lastLocation);

        lastUpdated = findViewById(R.id.lastUpdatedBtn);
        lastUpdated.bringToFront();
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        lastUpdatedText.bringToFront();
        lastUpdatedText.setVisibility(View.GONE);
        lastUpdated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getApplicationContext(), "Last location update received at " + date + ".  History available since " + hist + ".", Toast.LENGTH_LONG).show();
                if(lastUpdatedText.getVisibility() == View.VISIBLE) {
                    lastUpdatedText.setVisibility(View.GONE);
                } else {
                    Timestamp temp = new Timestamp(locationsList.get(0).getUsed_at());
                    Date date = temp;
                    Timestamp hist0 = new Timestamp(locationsList.get(locationsList.size()-1).getUsed_at());
                    Date hist = hist0;
                    lastUpdatedText.setText(Html.fromHtml("Last location update received at <b>" + date + "</b>.  \nHistory available since <b>" + hist + "</b>.", Html.FROM_HTML_MODE_LEGACY));
                    lastUpdatedText.setVisibility(View.VISIBLE);
                }
            }
        });

        RetrofitService retrofitService = new RetrofitService();
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long device_id = pref.getLong("device_id", -1);

        zoneApi.getAllZonesWithDetailsForDevice(token, device_id).enqueue(new Callback<ZonesForDeviceResponse>() {
            @Override
            public void onResponse(Call<ZonesForDeviceResponse> call, Response<ZonesForDeviceResponse> response) {
                if(response.isSuccessful()) {
                    lastLocationFragment.setGetZones(new ZonesListDelegation() {
                        @Override
                        public ZonesForDeviceResponse myMethod() {
                            return response.body();
                        }
                    });
                    historyFragment.setGetZones(new ZonesListDelegation() {
                        @Override
                        public ZonesForDeviceResponse myMethod() {
                            return response.body();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ZonesForDeviceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.d("1122", String.valueOf(t));
            }
        });
        lastLocationFragment.setGetEntry(new LocationEntryDelegation() {
            @Override
            public List<LocationEntry> myMethod() {
                List<LocationEntry> list = new ArrayList<>();
                if(locationsList != null && locationsList.size() != 0)
                {
                    list.add(locationsList.get(0));
                }
                else Log.d("1122", "gps activity/last location my method - location list null or empty");
                return list;
            }
        });

        historyFragment.setGetHistory(new LocationEntryDelegation() {
            @Override
            public List<LocationEntry> myMethod() {
                List<LocationEntry> list = new ArrayList<LocationEntry>();
                if(locationsList != null && locationsList.size() != 0)
                {
                    list.addAll(locationsList);
                }
                else Log.d("1122", "gps activity/history my method - location list null or empty");
                return list;
            }
        });

        radarFragment.setGetEntry(new LocationEntryDelegation() {
            @Override
            public List<LocationEntry> myMethod() {
                List<LocationEntry> list = new ArrayList<>();
                if(locationsList != null && locationsList.size() != 0)
                {
                    list.add(locationsList.get(0));
                }
                else Log.d("1122", "gps activity/radar my method - location list null or empty");
                return list;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.gpsActivity_fragcont, lastLocationFragment)
                .add(R.id.gpsActivity_fragcont, historyFragment)
                .add(R.id.gpsActivity_fragcont, radarFragment)
                .commit();
        getSupportFragmentManager().beginTransaction()
                .show(lastLocationFragment)
                .hide(historyFragment)
                .hide(radarFragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    LastLocationFragment lastLocationFragment = new LastLocationFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    RadarFragment radarFragment = new RadarFragment();

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;
    List<LocationEntry> locationsList;
    @Override
    protected void onResume() {

        handler.post(runnable = new Runnable() {
            public void run() {
                RetrofitService retrofitService = new RetrofitService();
                LocationEntryApi locationEntryApi = retrofitService.getRetrofit().create(LocationEntryApi.class);
                SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
                Long device_id = pref.getLong("device_id", -1);
                locationEntryApi.getHistoryForDevice(token, device_id)
                        .enqueue(new Callback<List<LocationEntry>>() {
                            @Override
                            public void onResponse(Call<List<LocationEntry>> call, Response<List<LocationEntry>> response) {
                                if(response.isSuccessful() && response.body() != null){
                                    locationsList.clear();
                                    locationsList.addAll(response.body());
                                }
                                else Log.d("1122", "gps activity - response null");
                            }
                            @Override
                            public void onFailure(Call<List<LocationEntry>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                Log.d("1122", String.valueOf(t));                                    }
                        });
                handler.postDelayed(runnable, delay);

            }
        });
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        Log.d("1122", "last location - on pause");
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.bottomNavigation_lastLocation:
                getSupportFragmentManager().beginTransaction()
                        .show(lastLocationFragment)
                        .hide(historyFragment)
                        .hide(radarFragment)
                        .commit();
                return true;

            case R.id.bottomNavigation_history:
                getSupportFragmentManager().beginTransaction()
                        .show(historyFragment)
                        .hide(lastLocationFragment)
                        .hide(radarFragment)
                        .commit();
                return true;

            case R.id.bottomNavigation_radar:
                getSupportFragmentManager().beginTransaction()
                        .show(radarFragment)
                        .hide(historyFragment)
                        .hide(lastLocationFragment)
                        .commit();
                return true;
        }
        return false;
    }
}