package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetHolder extends RecyclerView.ViewHolder {
    TextView name, devices;
    ImageButton edit, delete;
    ImageView photo;
    ConstraintLayout layout;
    public PetHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.petItem_name);
        devices = itemView.findViewById(R.id.petItem_appliedTo);
        edit = itemView.findViewById(R.id.petItem_edit);
        delete = itemView.findViewById(R.id.petItem_delete);
        photo = itemView.findViewById(R.id.petItem_picture);
        layout = itemView.findViewById(R.id.petItem_layout);

    }
}
