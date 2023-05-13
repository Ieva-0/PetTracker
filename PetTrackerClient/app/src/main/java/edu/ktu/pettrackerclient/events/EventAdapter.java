package edu.ktu.pettrackerclient.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.pets.PetApi;
import edu.ktu.pettrackerclient.pet_groups.PetGroupApi;
import edu.ktu.pettrackerclient.RetrofitService;
import edu.ktu.pettrackerclient.zones.ZoneApi;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {
    private List<EventWithDetails> eventList;
    private Context ctx;
    public EventAdapter(List<EventWithDetails> eventList, Context ctx) {
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
        EventWithDetails event = eventList.get(position);
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
        String type = event.getType().equals(1) ? " left " : event.getType().equals(2)? " entered " : " NONE ";
        String info_text = "Pet <b>" + event.getPet().getName() + "</b>" + type + "zone <b>" + event.getZone().getName() + "</b>";
        if(event.getPet_group() != null) {
            info_text += " (assigned to group <b>" + event.getPet_group().getName() + "</b>)";
        }
        holder.info.setText(Html.fromHtml(info_text, Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setItems(List<EventWithDetails> items) {
        eventList = items;
    }


}