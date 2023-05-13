package edu.ktu.pettrackerclient.users;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class UserHolder extends RecyclerView.ViewHolder {
    TextView name, info;
    ImageButton delete, block;
    ConstraintLayout layout;
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.userItem_name);
        info = itemView.findViewById(R.id.userItem_infoText);
        delete = itemView.findViewById(R.id.userItem_delete);
        block = itemView.findViewById(R.id.userItem_block);
    }
}
