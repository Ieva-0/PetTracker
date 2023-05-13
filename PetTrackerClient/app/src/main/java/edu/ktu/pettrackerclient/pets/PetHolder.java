package edu.ktu.pettrackerclient.pets;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetHolder extends RecyclerView.ViewHolder {
    TextView name, devices, details_device, details_zone, details_groups, details_notifications;
    ImageButton options;
    ImageView photo;
    ConstraintLayout layout;
    LinearLayout details;
    public PetHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.petItem_name);
        devices = itemView.findViewById(R.id.petItem_detailsText);
        options = itemView.findViewById(R.id.petItem_options);
        photo = itemView.findViewById(R.id.petItem_picture);
        layout = itemView.findViewById(R.id.petItem_layout);
        details = itemView.findViewById(R.id.petItem_details);
        details_device = itemView.findViewById(R.id.petItem_details_device);
        details_zone = itemView.findViewById(R.id.petItem_details_zone);
        details_groups = itemView.findViewById(R.id.petItem_details_groups);
        details_notifications = itemView.findViewById(R.id.petItem_details_notifications);
    }
}
