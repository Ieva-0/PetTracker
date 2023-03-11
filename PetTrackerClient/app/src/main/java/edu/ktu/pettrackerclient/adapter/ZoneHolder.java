package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class ZoneHolder extends RecyclerView.ViewHolder {
    TextView name, devices;
    ImageButton edit, delete;
    ConstraintLayout layout;
    public ZoneHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.zoneItem_name);
        devices = itemView.findViewById(R.id.zoneItem_appliedTo);
        edit = itemView.findViewById(R.id.zoneItem_edit);
        delete = itemView.findViewById(R.id.zoneItem_delete);
        layout = itemView.findViewById(R.id.zoneItem_layout);
    }
}
