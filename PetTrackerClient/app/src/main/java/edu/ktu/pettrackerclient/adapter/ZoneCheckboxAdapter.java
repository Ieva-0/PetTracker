package edu.ktu.pettrackerclient.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;

public class ZoneCheckboxAdapter  extends RecyclerView.Adapter<ZoneCheckboxHolder> {
    private List<Zone> zoneList;
    private Context ctx;
    private List<Zone> checkeditems;
    public ZoneCheckboxAdapter(List<Zone> zoneList, Context ctx, List<Zone> selected) {
        this.checkeditems = selected;
        this.zoneList = zoneList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ZoneCheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new ZoneCheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneCheckboxHolder holder, int position) {
        Zone zoneitem = zoneList.get(position);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        holder.zone.setText(zoneitem.getName());

        if(checkeditems.contains(zoneitem)) {
            holder.zone.setChecked(true);
        }
        holder.zone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.zone.isChecked()) {
                    checkeditems.add(zoneitem);
                } else {
                    if(checkeditems.contains(zoneitem)) {
                        checkeditems.remove(zoneitem);
                    }
                }
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.zone.setChecked(!holder.zone.isChecked());
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

    public List<Zone> getChecked() {
        return checkeditems;
    }
}
