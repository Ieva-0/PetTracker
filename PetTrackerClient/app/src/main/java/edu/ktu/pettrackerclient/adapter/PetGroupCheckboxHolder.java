package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class PetGroupCheckboxHolder extends RecyclerView.ViewHolder {
    CheckBox group;
    ConstraintLayout layout;
    public PetGroupCheckboxHolder(@NonNull View itemView) {
        super(itemView);
        group = itemView.findViewById(R.id.checkboxItem_checkbox);
        layout = itemView.findViewById(R.id.groupCreate_petItemLayout);

    }
}
