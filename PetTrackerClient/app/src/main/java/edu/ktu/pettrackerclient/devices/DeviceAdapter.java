package edu.ktu.pettrackerclient.devices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
    private List<DeviceWithDetails> deviceList;
    private Context ctx;
    TextView noResult;

    public DeviceAdapter(List<DeviceWithDetails> deviceList, Context ctx, TextView noResult) {
        this.deviceList = deviceList;
        this.ctx = ctx;
        this.noResult = noResult;
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
        DeviceWithDetails device = deviceList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);


        holder.name.setText(device.getName());

        if(device.getPet() != null) {
            holder.pet.setText("Assigned to pet " + device.getPet().getName());

            holder.delete.setBackground(ctx.getDrawable(R.drawable.circle_img_btn_emerald_disabled));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ctx, "Please unassign device to delete it.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.pet.setText("Not assigned to a pet.");

            holder.delete.setBackground(ctx.getDrawable(R.drawable.circle_img_btn_emerald));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                    builder.setTitle("Are you sure you'd like to delete this device?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deviceApi.deleteDevice(token, device.getId())
                                    .enqueue(new Callback<MessageResponse>() {
                                        @Override
                                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                            if (response.isSuccessful()) {
                                                if(response.body().isSuccessful()) {
                                                    deviceList.remove(holder.getAdapterPosition());
                                                    if(deviceList.size() > 0) {
                                                        notifyItemRemoved(holder.getAdapterPosition());
                                                        noResult.setVisibility(View.INVISIBLE);
                                                    }
                                                    else  {
                                                        notifyDataSetChanged();
                                                        noResult.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                                Toast.makeText(ctx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                                            Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
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
    public int getItemCount() { return deviceList.size(); }
    public void setItems(List<DeviceWithDetails> items) {
        deviceList = items;
    }

}