package edu.ktu.pettrackerclient.checkboxes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;

public class PetGroupCheckboxAdapter  extends RecyclerView.Adapter<CheckboxHolder> {
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
    public CheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new CheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxHolder holder, int position) {
        PetGroup groupitem = groupList.get(position);
        holder.checkbox.setText(groupitem.getName());

        if(checkeditems.contains(groupitem)) {
            holder.checkbox.setChecked(true);
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.checkbox.isChecked()) {
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
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
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
