package edu.ktu.pettrackerclient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ZoneAdapter extends RecyclerView.Adapter<ZoneHolder> {
    private List<Zone> zoneList;
    private Context ctx;
    public ZoneAdapter(List<Zone> zoneList, Context ctx) {
        this.zoneList = zoneList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ZoneHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_item, parent, false);

        return new ZoneHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneHolder holder, int position) {
        Zone zone = zoneList.get(position);
        holder.name.setText(zone.getName());
        holder.devices.setText(String.valueOf(zone.getId()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("device_id", device.getId());
//                Navigation.findNavController(view).navigate(R.id.list_to_gps, bundle);
//                SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putLong("zone_id", zone.getId()); // Storing string
//                editor.commit(); // commit changes
//                Intent intent = new Intent(ctx, GpsActivity.class);
//                intent.putExtra("device_id", device.getId());
//                ctx.startActivity(intent);
                Toast.makeText(ctx, "clicked zone" + zone.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        RetrofitService retrofitService = new RetrofitService();
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Are you sure you'd like to delete this zone?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zoneApi.deleteZone(token, zone.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(view.getContext(), "deleted successfully", Toast.LENGTH_SHORT).show();
                                        zoneList.remove(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), zoneList.size());
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
    }

    @Override
    public int getItemCount() {
        return zoneList.size();
    }

    public void setItems(List<Zone> items) {
        zoneList = items;
    }


}
