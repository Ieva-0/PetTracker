package edu.ktu.pettrackerclient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//import edu.ktu.pettrackerclient.databinding.ActivityMainBinding;
import edu.ktu.pettrackerclient.users.JwtResponse;
import edu.ktu.pettrackerclient.zones.zone_points.ZonePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ActionBar bar;
    ImageButton logout;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {

                    Log.d("1122", "not granted");
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.ACCESS_NETWORK_STATE);
        perms.add(Manifest.permission.INTERNET);
        perms.add(Manifest.permission.FOREGROUND_SERVICE);
        perms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        perms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        if (ContextCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED) {
//            if (!(ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                    PackageManager.PERMISSION_GRANTED)) {
//                requestPermissionLauncher.launch(
//                        Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (!(ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//                    PackageManager.PERMISSION_GRANTED)) {
//                requestPermissionLauncher.launch(
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//
//        } else {
//            // You can directly ask for the permission.
//            // The registered ActivityResultCallback gets the result of this request.
//            requestPermissionLauncher.launch(
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(
                    this, perm) ==
                    PackageManager.PERMISSION_GRANTED) {

            } else {

                requestPermissionLauncher.launch(
                        perm);
            }
        }
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        bar = getSupportActionBar();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.welcome_user_text);

        //-------- drawer menu
        SharedPreferences pref = this.getSharedPreferences("MyPref", 0); // 0 - for private mode
        navUsername.setText("Hello, " + pref.getString("username", null) + "!");
        Integer role = pref.getInt("role", 0);
        Log.d("1122", "role " + role);
        if(role.equals(1)) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.drawerNav_deviceList, R.id.drawerNav_zoneList, R.id.drawerNav_petList,
                    R.id.drawerNav_accountFragment, R.id.drawerNav_petGroupListFragment, R.id.drawerNav_eventList,
                    R.id.drawerNav_userList)
                    .setOpenableLayout(drawer)
                    .build();

        } else {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.drawerNav_deviceList, R.id.drawerNav_zoneList, R.id.drawerNav_petList,
                    R.id.drawerNav_accountFragment, R.id.drawerNav_petGroupListFragment, R.id.drawerNav_eventList)
                    .setOpenableLayout(drawer)
                    .build();

            navigationView.getMenu().getItem(6).setVisible(false);
        }
        NavController navController = Navigation.findNavController(this, R.id.mainActivity_fragmentCont);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //--------------

        //-------pet notifications
        startService(new Intent(this, DeviceZoneService.class));
        //--------------

        //-----logout
        logout = findViewById(R.id.logout_btn);
        Context c = this;
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AlertDialogTheme);
                builder.setTitle("Are you sure you'd like to log out?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = MainActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear().commit();
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();

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
    }
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.mainActivity_fragmentCont);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}