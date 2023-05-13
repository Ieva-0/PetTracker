package edu.ktu.pettrackerclient.pets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Base64;
import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.location_entries.GpsActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetAdapter extends RecyclerView.Adapter<PetHolder> {
    private List<PetWithDetails> petList;
    private Context ctx;
    TextView noResult;

    public PetAdapter(List<PetWithDetails> petList, Context ctx, TextView noResult) {
        this.petList = petList;
        this.ctx = ctx;
        this.noResult = noResult;

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
        PetWithDetails pet = petList.get(holder.getAdapterPosition());

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
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(ctx, holder.options);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.pet_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        switch(menuItem.getItemId()) {
                            case R.id.pet_popup_gps:
                                if(pet.getDevice() != null) {
                                    SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putLong("device_id", pet.getDevice().getId()); // Storing string
                                    editor.commit(); // commit changes
                                    Intent intent = new Intent(ctx, GpsActivity.class);
                                    ctx.startActivity(intent);
                                } else {
                                    Toast.makeText(ctx, "Please assign a device to see location information", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.pet_popup_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                                builder.setTitle("Are you sure you'd like to delete this pet?");
                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        petApi.deletePet(token, pet.getId())
                                                .enqueue(new Callback<MessageResponse>() {
                                                    @Override
                                                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                                        if (response.isSuccessful()) {
                                                            if(response.body().isSuccessful()) {
                                                                petList.remove(holder.getAdapterPosition());
                                                                if(petList.size() > 0) {
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
                                break;
                            case R.id.pet_popup_edit:
                                Bundle bundle = new Bundle();
                                bundle.putLong("pet_id", pet.getId());
                                if(pet.getDevice() != null ){
                                    bundle.putLong("fk_device_id", pet.getDevice().getId());
                                }
                                if(pet.getZone() != null ){
                                    bundle.putLong("fk_zone_id", pet.getZone().getId());
                                }
                                Navigation.findNavController(view).navigate(R.id.action_drawerNav_petList_to_petCreateFragment, bundle);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

//        //info in details
        if(pet.getDevice() != null) {
            holder.details_device.setText("Assigned device: " + pet.getDevice().getName());
        }
        if(pet.getZone() != null) {
            holder.details_zone.setText("Assigned zone: " +pet.getZone().getName());
        }
        String result = "Part of groups: ";
        for(PetGroup gr : pet.getGroups()) {
            if(pet.getGroups().indexOf(gr) != pet.getGroups().size()-1)
                result += gr.getName() + ", ";
            else result += gr.getName();
        }
        holder.details_groups.setText(result);


        if(pet.isNotifications())
            holder.details_notifications.setText("Notifications enabled");
        else holder.details_notifications.setText("Notifications disabled");


    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public void setItems(List<PetWithDetails> items) {
        petList = items;
    }

}
