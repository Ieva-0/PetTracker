package edu.ktu.pettrackerclient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.FileUploadApi;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.PetGroupApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupAdapter extends RecyclerView.Adapter<PetGroupHolder> {
    private List<PetGroup> petGroupList;
    private Context ctx;
    public PetGroupAdapter(List<PetGroup> petGroupList, Context ctx) {
        this.petGroupList = petGroupList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PetGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_group_item, parent, false);

        return new PetGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetGroupHolder holder, int position) {
        PetGroup petGroup = petGroupList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        PetGroupApi petGroupApi = retrofitService.getRetrofit().create(PetGroupApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        petGroupApi.getPetAmountInGroup(token, petGroup.getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer petamount = response.body();
                if(petGroup.getFk_zone_id() != null) {
                    ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
                    zoneApi.getZoneById(token, petGroup.getFk_zone_id())
                            .enqueue(new Callback<Zone>() {
                                @Override
                                public void onResponse(Call<Zone> call, Response<Zone> response) {
                                    Zone zone = response.body();
                                    if(zone != null && zone.getName() != null) {
                                        holder.info.setText(petamount.toString() + " pet(s) in group. Applied zone: " + zone.getName() +". Notifications " + (petGroup.isNotifications() ? "enabled." : "disabled."));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Zone> call, Throwable t) {

                                }
                            });
                } else {
                    holder.info.setText(petamount.toString() + " pet(s) in group. No zone applied. Notifications " + (petGroup.isNotifications() ? "enabled." : "disabled."));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        holder.name.setText(petGroup.getName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Are you sure you'd like to delete this pet group?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        petGroupApi.deletePetGroup(token, petGroup.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(view.getContext(), "deleted successfully", Toast.LENGTH_SHORT).show();
                                        Log.d("1122", "adapter position " + holder.getAdapterPosition());
                                        petGroupList.remove(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), petGroupList.size());
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
                bundle.putLong("pet_group_id", petGroup.getId());
                Navigation.findNavController(view).navigate(R.id.action_drawerNav_petGroupListFragment_to_petGroupCreateFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petGroupList.size();
    }

    public void setItems(List<PetGroup> items) {
        petGroupList = items;
    }

}
