package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetCheckboxHolder extends RecyclerView.ViewHolder {
    CheckBox pet;
    ConstraintLayout layout;
    public PetCheckboxHolder(@NonNull View itemView) {
        super(itemView);
        pet = itemView.findViewById(R.id.checkboxItem_checkbox);
        layout = itemView.findViewById(R.id.groupCreate_petItemLayout);

    }
}
