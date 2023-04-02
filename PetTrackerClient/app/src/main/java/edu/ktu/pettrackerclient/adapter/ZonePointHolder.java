package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class ZonePointHolder extends RecyclerView.ViewHolder {
    TextView name, id;
    ImageButton edit, delete;
    ConstraintLayout layout;
    public ZonePointHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.zonePointItem_name);
        id = itemView.findViewById(R.id.zonePointItem_id);
        edit = itemView.findViewById(R.id.zonePointItem_edit);
        delete = itemView.findViewById(R.id.zonePointItem_delete);
        layout = itemView.findViewById(R.id.zonePointItem_layout);
    }
}