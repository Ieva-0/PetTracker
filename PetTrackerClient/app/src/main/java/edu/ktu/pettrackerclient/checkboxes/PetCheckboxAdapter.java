package edu.ktu.pettrackerclient.checkboxes;

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
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.pets.PetApi;
import edu.ktu.pettrackerclient.RetrofitService;

public class PetCheckboxAdapter extends RecyclerView.Adapter<CheckboxHolder> {
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
    public CheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new CheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxHolder holder, int position) {
        Pet petitem = petList.get(position);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        holder.checkbox.setText(petitem.getName());

        if(checkeditems.contains(petitem)) {
            holder.checkbox.setChecked(true);
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.checkbox.isChecked()) {
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
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
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
