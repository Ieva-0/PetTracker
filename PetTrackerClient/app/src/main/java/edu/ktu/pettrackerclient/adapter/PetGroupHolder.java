package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetGroupHolder extends RecyclerView.ViewHolder {
    TextView name, info;
    ImageButton edit, delete;
    ConstraintLayout layout;
    public PetGroupHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.petGroupItem_name);
        info = itemView.findViewById(R.id.petGroupItem_appliedZone);
        edit = itemView.findViewById(R.id.petGroupItem_edit);
        delete = itemView.findViewById(R.id.petGroupItem_delete);
        layout = itemView.findViewById(R.id.petGroupItem_layout);

    }
}
