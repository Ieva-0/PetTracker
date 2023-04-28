package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.databinding.ActivityGpsBinding;
import edu.ktu.pettrackerclient.databinding.ActivityMainBinding;
import edu.ktu.pettrackerclient.model.LocationEntry;
import edu.ktu.pettrackerclient.retrofit.LocationEntryApi;
import edu.ktu.pettrackerclient.retrofit.LocationEntryDelegation;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        locationsList = new ArrayList<LocationEntry>();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_lastLocation);

        lastLocationFragment.setGetEntry(new LocationEntryDelegation() {
            @Override
            public List<LocationEntry> myMethod() {
                List<LocationEntry> list = new ArrayList<LocationEntry>();
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
                                Log.d("1122", response.body().toString());
                                if(response.isSuccessful() && response.body() != null){
                                    locationsList.clear();
                                    locationsList.addAll(response.body());
                                }
                                else Log.d("1122", "gps activity - response null");
                            }
                            @Override
                            public void onFailure(Call<List<LocationEntry>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "failed to retrieve locations", Toast.LENGTH_SHORT).show();
                            }
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