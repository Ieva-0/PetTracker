package edu.ktu.pettrackerclient.pet_groups;

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
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupAdapter extends RecyclerView.Adapter<PetGroupHolder> {
    private List<PetGroupWithDetails> petGroupList;
    private Context ctx;
    TextView noResult;
    public PetGroupAdapter(List<PetGroupWithDetails> petGroupList, Context ctx, TextView noResult) {
        this.petGroupList = petGroupList;
        this.ctx = ctx;
        this.noResult = noResult;
    }

    @NonNull
    @Override
    public PetGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_group_item, parent, false);

        return new PetGroupHolder(view);
    }

    int mExpandedPosition = -1;

    @Override
    public void onBindViewHolder(@NonNull PetGroupHolder holder, int position) {
        PetGroupWithDetails petGroup = petGroupList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        PetGroupApi petGroupApi = retrofitService.getRetrofit().create(PetGroupApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        if(petGroup.getZone() != null) {
            holder.zone.setText("Applied zone: " + petGroup.getZone().getName());
        } else {
            holder.zone.setText("No zone applied");
        }
        if(petGroup.getPets() != null) {
            String result = petGroup.getPets().size() + " pets in group: ";
            for(Pet p : petGroup.getPets()) {
                if(petGroup.getPets().indexOf(p) != petGroup.getPets().size()-1)
                    result += p.getName() + ", ";
                else result += p.getName();
            }
            holder.pets.setText(result);
        } else {
            holder.zone.setText("No pets in group.");
        }
        if(petGroup.isNotifications())
            holder.notifications.setText("Notifications enabled");
        else holder.notifications.setText("Notifications disabled");


        holder.name.setText(petGroup.getName());

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                builder.setTitle("Are you sure you'd like to delete this pet group?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        petGroupApi.deletePetGroup(token, petGroup.getId())
                                .enqueue(new Callback<MessageResponse>() {
                                    @Override
                                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                        if (response.isSuccessful()) {
                                            if(response.body().isSuccessful()) {
                                                petGroupList.remove(holder.getAdapterPosition());
                                                if(petGroupList.size() > 0) {
                                                    notifyItemRangeChanged(holder.getAdapterPosition(), petGroupList.size());
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

    public void setItems(List<PetGroupWithDetails> items) {
        petGroupList = items;
    }

}
