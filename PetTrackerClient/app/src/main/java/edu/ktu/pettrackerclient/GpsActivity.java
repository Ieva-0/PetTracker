package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import edu.ktu.pettrackerclient.databinding.ActivityGpsBinding;
import edu.ktu.pettrackerclient.databinding.ActivityMainBinding;

public class GpsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    long device_id;
    BottomNavigationView bottomNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityGpsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_lastLocation);
//        device_id = getIntent().getLongExtra("device_id");
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putLong("device_id", device_id); // Storing string
//        editor.commit(); // commit changes

//        Bundle bundle = new Bundle();
//        bundle.putString("device_id", device_id);
//        Toast.makeText(this, device_id, Toast.LENGTH_SHORT).show();
//        lastLocationFragment.setArguments(bundle);
//        historyFragment.setArguments(bundle);
//        radarFragment.setArguments(bundle);

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

//        Toast.makeText(this, getIntent().getStringExtra("device_id"), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, device_id, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get location info here?

    }
    LastLocationFragment lastLocationFragment = new LastLocationFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    RadarFragment radarFragment = new RadarFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


//        Bundle bundle = new Bundle();
//        bundle.putString("device_id", device_id);
////        Toast.makeText(this, device_id, Toast.LENGTH_SHORT).show();
//        lastLocationFragment.setArguments(bundle);
//        historyFragment.setArguments(bundle);
//        radarFragment.setArguments(bundle);


        Log.d("1122", "im in nav item select" );
        switch (item.getItemId()) {
            case R.id.bottomNavigation_lastLocation:
//                lastLocationFragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, lastLocationFragment).commit();
                getSupportFragmentManager().beginTransaction()
                        .show(lastLocationFragment)
                        .hide(historyFragment)
                        .hide(radarFragment)
                        .commit();
                return true;

            case R.id.bottomNavigation_history:
//                historyFragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, historyFragment).commit();
                getSupportFragmentManager().beginTransaction()
                        .show(historyFragment)
                        .hide(lastLocationFragment)
                        .hide(radarFragment)
                        .commit();
                return true;

            case R.id.bottomNavigation_radar:
//                radarFragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, radarFragment).commit();
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