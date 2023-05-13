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
import edu.ktu.pettrackerclient.zones.Zone;

public class ZoneCheckboxAdapter  extends RecyclerView.Adapter<CheckboxHolder> {
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
    public CheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_item, parent, false);

        return new CheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxHolder holder, int position) {
        Zone zoneitem = zoneList.get(position);

        holder.checkbox.setText(zoneitem.getName());

        if(checkeditems.contains(zoneitem)) {
            holder.checkbox.setChecked(true);
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.checkbox.isChecked()) {
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
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
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
