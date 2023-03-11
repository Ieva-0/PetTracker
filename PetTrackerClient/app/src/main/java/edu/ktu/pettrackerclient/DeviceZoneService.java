package edu.ktu.pettrackerclient;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.MyLine;
import edu.ktu.pettrackerclient.model.MyPolygon;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import edu.ktu.pettrackerclient.retrofit.ZonePointApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceZoneService extends Service {
    public DeviceZoneService() {
    }
    int startMode;       // indicates how to behave if the service is killed
    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        mStatusChecker.run();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return allowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                actions(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                serviceHandler.postDelayed(mStatusChecker, 10000);
            }
        }
    };

    public boolean insidePolygon(LatLng p, MyPolygon shape) {
        MyLine ray = new MyLine(p, new LatLng(minLat(shape)-1, minLong(shape)-1));
        int intersections = 0;
        for(int i = 0; i < shape.lines.size(); i++) {
            if(doIntersect(ray, shape.lines.get(i))) {
                intersections++;
            }
        }
        if(intersections % 2 == 0) { // outside
            return false;
        } else { // inside
            return true;
        }
    }
    public boolean doIntersect(MyLine il1, MyLine il2) {

        if(il1.insertIntoEquation(il2.p1) > 0 == il1.insertIntoEquation(il2.p2) > 0) {
            //does not intersect
            return false;
        }

        if(il2.insertIntoEquation(il1.p1) > 0 == il2.insertIntoEquation(il1.p2) > 0) {
            //does not intersect
            return false;
        }

        if((il1.a * il2.b) - (il2.a * il1.b) == 0.0) {
            // collinear
            return false;
        }

        return true;
    }
    public double minLat(MyPolygon p) {
        double minLat = p.polygon_points.get(0).latitude;
        for(int i = 0; i < p.polygon_points.size(); i++) {
            if(p.polygon_points.get(i).latitude < minLat) {
                minLat = p.polygon_points.get(i).latitude;
            }
        }
        return minLat;
    }

    public double minLong(MyPolygon p) {
        double minLong = p.polygon_points.get(0).longitude;
        for(int i = 0; i < p.polygon_points.size(); i++) {
            if(p.polygon_points.get(i).longitude < minLong) {
                minLong = p.polygon_points.get(i).longitude;
            }
        }
        return minLong;
    }
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    public void actions() {
        Log.d("1122", "in action");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_gallery)
                .setContentTitle("hi there")
                .setContentText("uwu")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        RetrofitService retrofitService = new RetrofitService();
        ZonePointApi zoneApi = retrofitService.getRetrofit().create(ZonePointApi.class);
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        List<Device> devices = new ArrayList<>();
        deviceApi.getAllDevicesForUser(token, user_id)
                .enqueue(new retrofit2.Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {

                        response.body().forEach(o -> {
                            if(o.getFk_zone_id() != null)
                                devices.add(o);
                        });
                        if(devices.size() > 0) {
                            for (Device d : devices) {
                                zoneApi.getZonePointsForZone(token, d.getFk_zone_id())
                                        .enqueue(new retrofit2.Callback<List<ZonePoint>>() {
                                            @Override
                                            public void onResponse(Call<List<ZonePoint>> call, Response<List<ZonePoint>> response) {
                                                if(response.body() != null) {
                                                    Log.d("1122", response.body().toString());

                                                    List<LatLng> polygonPoints = new ArrayList<>();
                                                    for (ZonePoint zonePoint : response.body()) {
                                                        polygonPoints.add(new LatLng(zonePoint.getLatitude(), zonePoint.getLongitude()));
                                                    }

                                                    MyPolygon polygon = new MyPolygon(polygonPoints);

                                                    NotificationChannel channel = new NotificationChannel("hi", "mychannel", NotificationManager.IMPORTANCE_DEFAULT);
                                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                                    manager.createNotificationChannel(channel);
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "hi")
                                                            .setSmallIcon(R.drawable.ic_menu_gallery)
                                                            .setContentTitle("title")
                                                            .setContentText("text uwu");
                                                    notification = builder.build();
                                                    notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                                    notificationManagerCompat.notify(1, notification);

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<List<ZonePoint>> call, Throwable t) {

                                            }
                                        });
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {

                    }
                });
    }


    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }


    }
}