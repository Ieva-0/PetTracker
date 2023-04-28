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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.PetGroupApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetAdapter extends RecyclerView.Adapter<PetHolder> {
    private List<Pet> petList;
    private Context ctx;
    public PetAdapter(List<Pet> petList, Context ctx) {
        this.petList = petList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_item, parent, false);

        return new PetHolder(view);
    }

    int mExpandedPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull PetHolder holder, int position) {
        Pet pet = petList.get(holder.getAdapterPosition());

        RetrofitService retrofitService = new RetrofitService();
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);

        holder.name.setText(pet.getName());

        // set picture
        if(pet.getPicture() != null) {

            byte[] arr = Base64.getDecoder().decode(pet.getPicture());
            Bitmap b2 = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            if(b2 != null) {
                holder.photo.setImageBitmap(Bitmap.createScaledBitmap(b2, 120, 120, false));
            }

        } else {
            holder.photo.setImageResource(R.drawable.baseline_no_photography_24);

        }

        //expand/collapse details
        final boolean isExpanded = holder.getAdapterPosition()==mExpandedPosition;
        holder.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("112233", "position in onclick: " +holder.getAdapterPosition());

                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Are you sure you'd like to delete this pet?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        petApi.deletePet(token, pet.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(view.getContext(), "deleted successfully", Toast.LENGTH_SHORT).show();
                                        petList.remove(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), petList.size());
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
                bundle.putLong("pet_id", pet.getId());
                if(pet.getFk_device_id() != null ){
                    bundle.putLong("fk_device_id", pet.getFk_device_id());
                }
                if(pet.getFk_zone_id() != null ){
                    bundle.putLong("fk_zone_id", pet.getFk_zone_id());
                }
                Navigation.findNavController(view).navigate(R.id.action_drawerNav_petList_to_petCreateFragment, bundle);
            }
        });

        //info in details
        if(pet.getFk_device_id() != null) {
            DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
            deviceApi.getDeviceById(token, pet.getFk_device_id()).enqueue(new Callback<Device>() {
                @Override
                public void onResponse(Call<Device> call, Response<Device> response) {
                    if(response.isSuccessful())
                        holder.details_device.setText("Assigned device: " + response.body().getName());
                }

                @Override
                public void onFailure(Call<Device> call, Throwable t) {

                }
            });
        }
        if(pet.getFk_zone_id() != null) {
            ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
            zoneApi.getZoneById(token, pet.getFk_zone_id()).enqueue(new Callback<Zone>() {
                @Override
                public void onResponse(Call<Zone> call, Response<Zone> response) {
                    if(response.isSuccessful())
                        holder.details_zone.setText("Assigned zone: " + response.body().getName());
                }

                @Override
                public void onFailure(Call<Zone> call, Throwable t) {

                }
            });
        }

        PetGroupApi petGroupApi = retrofitService.getRetrofit().create(PetGroupApi.class);
        List<PetGroup> groups = new ArrayList<>();
        petGroupApi.getGroupsForPet(token, pet.getId()).enqueue(new Callback<List<PetGroup>>() {
            @Override
            public void onResponse(Call<List<PetGroup>> call, Response<List<PetGroup>> response) {
                if(response.isSuccessful() && response.body().size() > 0) {
                    String result = "Part of groups: ";
                    for(PetGroup gr : response.body()) {
                        if(response.body().indexOf(gr) != response.body().size()-1)
                            result += gr.getName() + ", ";
                        else result += gr.getName();
                    }
                    holder.details_groups.setText(result);
                }
            }

            @Override
            public void onFailure(Call<List<PetGroup>> call, Throwable t) {

            }
        });

        if(pet.isNotifications())
            holder.details_notifications.setText("Notifications enabled");
        else holder.details_notifications.setText("Notifications disabled");

        //
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


    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public void setItems(List<Pet> items) {
        petList = items;
    }

}
