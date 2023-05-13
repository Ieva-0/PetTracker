package edu.ktu.pettrackerclient.users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {
    private List<User> userList;
    private Context ctx;
    TextView noResult;
    private List<Role> roles;

    public UserAdapter(List<User> userList, Context ctx, List<Role> roles, TextView noResult) {
        this.userList = userList;
        this.ctx = ctx;
        this.roles = roles;
        this.noResult = noResult;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = userList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);

        holder.name.setText(user.getUsername());

        String infotext = "";
        for (Role r : roles) {
            if (r.getId().equals(user.getFk_role_id())) {
                infotext += r.getName() + ", ";
            }
        }
        infotext += user.isBlocked() ? "blocked" : "active";

        holder.info.setText(infotext);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                builder.setTitle("Are you sure you'd like to delete this user?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userApi.deleteUser(token, user.getId())
                                .enqueue(new Callback<MessageResponse>() {
                                    @Override
                                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                        if (response.isSuccessful()) {
                                            if(response.body().isSuccessful()) {
                                                userList.remove(holder.getAdapterPosition());
                                                if (userList.size() > 0) {
                                                    notifyItemRemoved(holder.getAdapterPosition());
                                                    noResult.setVisibility(View.INVISIBLE);
                                                } else {
                                                    notifyDataSetChanged();
                                                    noResult.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            Toast.makeText(ctx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                                        Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        Log.d("1122", String.valueOf(t));
                                        dialog.dismiss();
                                    }
                                });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if (user.isBlocked()) {
            holder.block.setColorFilter(Color.RED);
        } else {
            holder.block.setColorFilter(Color.BLACK);

        }
        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
                if (user.isBlocked())
                    builder.setTitle("Are you sure you'd like to unblock this user?");
                else builder.setTitle("Are you sure you'd like to block this user?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userApi.toggleBlockUser(token, user.getId()).enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().isSuccessful()) {
                                        userList.get(holder.getAdapterPosition()).setBlocked(!user.isBlocked());
                                        notifyItemChanged(holder.getAdapterPosition());
                                    }
                                    Toast.makeText(ctx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MessageResponse> call, Throwable t) {
                                Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                Log.d("1122", String.valueOf(t));
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
        return userList.size();
    }

    public void setItems(List<User> items) {
        userList = items;
    }
}
