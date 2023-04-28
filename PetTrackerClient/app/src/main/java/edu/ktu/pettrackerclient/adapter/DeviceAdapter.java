package edu.ktu.pettrackerclient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.GpsActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
    private List<Device> deviceList;
    private Context ctx;
    public DeviceAdapter(List<Device> deviceList, Context ctx) {
        this.deviceList = deviceList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);

        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        Device device = deviceList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

//        if(device.getFk_zone_id() != null) {
//            ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
//            zoneApi.getZoneById(token, device.getFk_zone_id())
//                    .enqueue(new Callback<Zone>() {
//                        @Override
//                        public void onResponse(Call<Zone> call, Response<Zone> response) {
//                            Zone zone = response.body();
//                            if(zone != null && zone.getName() != null) {
//                                holder.zones.setText("Applied zone: " + zone.getName());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Zone> call, Throwable t) {
//
//                        }
//                    });
//        }

        holder.name.setText(device.getName());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("device_id", device.getId());
//                Navigation.findNavController(view).navigate(R.id.list_to_gps, bundle);
                SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("device_id", device.getId()); // Storing string
                editor.commit(); // commit changes
                Intent intent = new Intent(ctx, GpsActivity.class);
//                intent.putExtra("device_id", device.getId());
                ctx.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Are you sure you'd like to delete this device?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceApi.deleteDevice(token, device.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(view.getContext(), "deleted successfully", Toast.LENGTH_SHORT).show();
                                        deviceList.remove(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), deviceList.size());
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(view.getContext(), "failed", Toast.LENGTH_SHORT).show();
                                        Log.d("1122", t.toString());
                                    }
                                });

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
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("device_id", device.getId());
                Navigation.findNavController(view).navigate(R.id.action_drawerNav_deviceList_to_deviceEditFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void setItems(List<Device> items) {
        deviceList = items;
    }


}