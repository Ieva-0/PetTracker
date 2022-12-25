package edu.ktu.pettrackerclient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GpsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    String device_id;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_features);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
//        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_history);
        device_id = getIntent().getStringExtra("device_id");
        onNavigationItemSelected(new MenuItem() {
            @Override
            public int getItemId() {
                return R.id.bottomNavigation_lastLocation;
            }

            @Override
            public int getGroupId() {
                return 0;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence charSequence) {
                return null;
            }

            @Override
            public MenuItem setTitle(int i) {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence charSequence) {
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                return null;
            }

            @Override
            public MenuItem setIcon(Drawable drawable) {
                return null;
            }

            @Override
            public MenuItem setIcon(int i) {
                return null;
            }

            @Override
            public Drawable getIcon() {
                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {
                return null;
            }

            @Override
            public Intent getIntent() {
                return null;
            }

            @Override
            public MenuItem setShortcut(char c, char c1) {
                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char c) {
                return null;
            }

            @Override
            public char getNumericShortcut() {
                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char c) {
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean b) {
                return null;
            }

            @Override
            public boolean isCheckable() {
                return false;
            }

            @Override
            public MenuItem setChecked(boolean b) {
                return null;
            }

            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public MenuItem setVisible(boolean b) {
                return null;
            }

            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public MenuItem setEnabled(boolean b) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public boolean hasSubMenu() {
                return false;
            }

            @Override
            public SubMenu getSubMenu() {
                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                return null;
            }

            @Override
            public void setShowAsAction(int i) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int i) {
                return null;
            }

            @Override
            public MenuItem setActionView(View view) {
                return null;
            }

            @Override
            public MenuItem setActionView(int i) {
                return null;
            }

            @Override
            public View getActionView() {
                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {
                return null;
            }

            @Override
            public ActionProvider getActionProvider() {
                return null;
            }

            @Override
            public boolean expandActionView() {
                return false;
            }

            @Override
            public boolean collapseActionView() {
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
                return null;
            }
        });
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

        Bundle bundle = new Bundle();
        bundle.putString("device_id", device_id);
//        Toast.makeText(this, device_id, Toast.LENGTH_SHORT).show();
        Log.d("1122", "im in nav item select" );
        switch (item.getItemId()) {
            case R.id.bottomNavigation_lastLocation:
                lastLocationFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, lastLocationFragment).commit();
                return true;

            case R.id.bottomNavigation_history:
                historyFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, historyFragment).commit();
                return true;

            case R.id.bottomNavigation_radar:
                radarFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.gpsActivity_fragcont, radarFragment).commit();
                return true;
        }
        return false;
    }
}