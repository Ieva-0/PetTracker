package edu.ktu.pettrackerclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.DeviceListFragment;
import edu.ktu.pettrackerclient.GpsActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.test;

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
        Log.d("1122", String.valueOf(deviceList));
        Device device = deviceList.get(position);
        holder.name.setText(device.getName());
        holder.id.setText(device.getId());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("device_id", device.getId());
//                Navigation.findNavController(view).navigate(R.id.list_to_gps, bundle);
                Intent intent = new Intent(ctx, GpsActivity.class);
                intent.putExtra("device_id", device.getId());
                ctx.startActivity(intent);
            }
        });
//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), String.valueOf(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void setItems(List<Device> items) {
        deviceList = items;
    }


}