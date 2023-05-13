package edu.ktu.pettrackerclient.zones;

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
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneHolder> {
    private List<ZoneWithDetails> zoneList;
    private Context ctx;
    TextView noResult;

    public ZoneAdapter(List<ZoneWithDetails> zoneList, Context ctx, TextView noResult) {
        this.zoneList = zoneList;
        this.ctx = ctx;
        this.noResult = noResult;
    }
    @NonNull
    @Override
    public ZoneHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_item, parent, false);

        return new ZoneHolder(view);
    }

    int mExpandedPosition = -1;

    @Override
    public void onBindViewHolder(@NonNull ZoneHolder holder, int position) {
        RetrofitService retrofitService = new RetrofitService();
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        ZoneWithDetails zone = zoneList.get(position);
        holder.name.setText(zone.getName());

        if(zone.getGroups().size() > 0) {
            String result = "Assigned to groups: ";
            for(PetGroup gr : zone.getGroups()) {
                if(zone.getGroups().indexOf(gr) != zone.getGroups().size()-1)
                    result += gr.getName() + ", ";
                else result += gr.getName();
            }
            holder.groups.setText(result);
        } else {
            holder.groups.setText("Not assigned to any groups.");
        }

        if(zone.getPets().size() > 0) {
            String result = "Assigned to pets: ";
            for(Pet p : zone.getPets()) {
                if(zone.getGroups().indexOf(p) != zone.getGroups().size()-1)
                    result += p.getName() + ", ";
                else result += p.getName();
            }
            holder.pets.setText(result);
            holder.delete.setBackground(ctx.getDrawable(R.drawable.circle_img_btn_emerald_disabled));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ctx, "Please unassign zone to delete it.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder.pets.setText("Not assigned to any pets.");
            holder.delete.setBackground(ctx.getDrawable(R.drawable.circle_img_btn_emerald));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                    builder.setTitle("Are you sure you'd like to delete this zone?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            zoneApi.deleteZone(token, zone.getId())
                                    .enqueue(new Callback<MessageResponse>() {
                                        @Override
                                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                            if (response.isSuccessful()) {
                                                if(response.body().isSuccessful()) {
                                                    zoneList.remove(holder.getAdapterPosition());
                                                    if(zoneList.size() > 0) {
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
                                            Toast.makeText(ctx, "Failed to delete zone.", Toast.LENGTH_SHORT).show();
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
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("zone_id", zone.getId());
                Navigation.findNavController(view).navigate(R.id.action_drawerNav_zoneList_to_drawerNav_createZone, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return zoneList.size();
    }
    public void setItems(List<ZoneWithDetails> items) {
        zoneList = items;
    }
}
