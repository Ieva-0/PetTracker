package edu.ktu.pettrackerclient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import edu.ktu.pettrackerclient.GpsActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Event;
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

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {
    private List<Event> eventList;
    private Context ctx;
    public EventAdapter(List<Event> eventList, Context ctx) {
        this.eventList = eventList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);

        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        Event event = eventList.get(position);
        RetrofitService retrofitService = new RetrofitService();
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        PetGroupApi groupApi = retrofitService.getRetrofit().create(PetGroupApi.class);
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        Timestamp temp = new Timestamp(event.getTimestamp());
        Date date = temp;
        holder.date.setText(temp.toString());
        String type = event.getFk_type() == 1 ? " escaped from " : event.getFk_type() == 2 ? " returned to " : " NONE ";
        petApi.getPetById(token, event.getFk_pet_id()).enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                Pet pet = response.body();
                zoneApi.getZoneById(token, event.getFk_zone_id()).enqueue(new Callback<Zone>() {
                    @Override
                    public void onResponse(Call<Zone> call, Response<Zone> response) {
                        Zone zone = response.body();
                        if(event.getFk_group_id() != null) {
                            groupApi.getPetGroupById(token, event.getFk_group_id()).enqueue(new Callback<PetGroup>() {
                                @Override
                                public void onResponse(Call<PetGroup> call, Response<PetGroup> response) {
                                    PetGroup group = response.body();
                                    String info_text = "Pet <b>" + pet.getName() + "</b>" + type + "zone <b>" + zone.getName() + "</b> (assigned to group <b>" + group.getName() + "</b>)";
                                    holder.info.setText(Html.fromHtml(info_text, Html.FROM_HTML_MODE_LEGACY));
                                }

                                @Override
                                public void onFailure(Call<PetGroup> call, Throwable t) {

                                }
                            });
                        } else {
                            String info_text = "Pet <b>" + pet.getName() + "</b>" + type + "zone <b>" + zone.getName() + "</b>";
                            holder.info.setText(Html.fromHtml(info_text, Html.FROM_HTML_MODE_LEGACY));
                        }
                    }

                    @Override
                    public void onFailure(Call<Zone> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setItems(List<Event> items) {
        eventList = items;
    }


}