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
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;

public class PetCheckboxAdapter extends RecyclerView.Adapter<PetCheckboxHolder> {
    private List<Pet> petList;
    private Context ctx;
    private List<Pet> checkeditems;
    public PetCheckboxAdapter(List<Pet> petList, Context ctx, List<Pet> selected) {
        this.checkeditems = selected;
        this.petList = petList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PetCheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new PetCheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetCheckboxHolder holder, int position) {
        Pet petitem = petList.get(position);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        holder.pet.setText(petitem.getName());

        if(checkeditems.contains(petitem)) {
            holder.pet.setChecked(true);
        }
        holder.pet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.pet.isChecked()) {
                    checkeditems.add(petitem);
                } else {
                    if(checkeditems.contains(petitem)) {
                        checkeditems.remove(petitem);
                    }
                }
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.pet.setChecked(!holder.pet.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public void setItems(List<Pet> items) {
        petList = items;
    }

    public List<Pet> getChecked() {
        return checkeditems;
    }
}
