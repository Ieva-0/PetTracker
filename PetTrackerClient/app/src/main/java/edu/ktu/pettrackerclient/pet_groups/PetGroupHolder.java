package edu.ktu.pettrackerclient.pet_groups;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetGroupHolder extends RecyclerView.ViewHolder {
    TextView name, notifications, pets, zone;
    ImageButton edit, delete;
    LinearLayout details;
    public PetGroupHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.petGroupItem_name);
        edit = itemView.findViewById(R.id.petGroupItem_edit);
        delete = itemView.findViewById(R.id.petGroupItem_delete);
        details = itemView.findViewById(R.id.petGroupItem_details);
        pets = itemView.findViewById(R.id.petGroupItem_details_pets);
        notifications = itemView.findViewById(R.id.petGroupItem_details_notifications);
        zone = itemView.findViewById(R.id.petGroupItem_details_zone);
    }
}
