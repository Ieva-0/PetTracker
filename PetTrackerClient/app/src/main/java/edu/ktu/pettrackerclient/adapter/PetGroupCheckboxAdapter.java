package edu.ktu.pettrackerclient.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;

public class PetGroupCheckboxAdapter  extends RecyclerView.Adapter<PetGroupCheckboxHolder> {
    private List<PetGroup> groupList;
    private Context ctx;
    private List<PetGroup> checkeditems;
    public PetGroupCheckboxAdapter(List<PetGroup> petList, Context ctx, List<PetGroup> selected) {
        this.checkeditems = selected;
        this.groupList = petList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PetGroupCheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new PetGroupCheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetGroupCheckboxHolder holder, int position) {
        PetGroup groupitem = groupList.get(position);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        holder.group.setText(groupitem.getName());

        if(checkeditems.contains(groupitem)) {
            holder.group.setChecked(true);
        }
        holder.group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.group.isChecked()) {
                    checkeditems.add(groupitem);
                } else {
                    if(checkeditems.contains(groupitem)) {
                        checkeditems.remove(groupitem);
                    }
                }
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.group.setChecked(!holder.group.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setItems(List<PetGroup> items) {
        groupList = items;
    }

    public List<PetGroup> getChecked() {
        return checkeditems;
    }
}
