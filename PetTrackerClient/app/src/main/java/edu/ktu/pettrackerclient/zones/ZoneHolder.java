package edu.ktu.pettrackerclient.zones;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class ZoneHolder extends RecyclerView.ViewHolder {
    TextView name, groups, pets;
    ImageButton edit, delete;
    LinearLayout details;
    public ZoneHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.zoneItem_name);
        groups = itemView.findViewById(R.id.zoneItem_details_groups);
        pets = itemView.findViewById(R.id.zoneItem_details_pets);
        edit = itemView.findViewById(R.id.zoneItem_edit);
        delete = itemView.findViewById(R.id.zoneItem_delete);
        details = itemView.findViewById(R.id.zoneItem_details);
    }
}
