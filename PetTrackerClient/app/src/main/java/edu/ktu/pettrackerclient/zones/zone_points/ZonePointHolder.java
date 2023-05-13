package edu.ktu.pettrackerclient.zones.zone_points;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class ZonePointHolder extends RecyclerView.ViewHolder {
    TextView name, id;
    ImageButton delete;
    ConstraintLayout layout;
    public ZonePointHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.zonePointItem_name);
        id = itemView.findViewById(R.id.zonePointItem_id);
        delete = itemView.findViewById(R.id.zonePointItem_delete);
        layout = itemView.findViewById(R.id.zonePointItem_layout);
    }
}