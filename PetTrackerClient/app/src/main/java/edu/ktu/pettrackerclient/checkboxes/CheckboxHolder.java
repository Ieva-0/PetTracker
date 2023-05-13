package edu.ktu.pettrackerclient.checkboxes;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class CheckboxHolder extends RecyclerView.ViewHolder {
    CheckBox checkbox;
    ConstraintLayout layout;
    public CheckboxHolder(@NonNull View itemView) {
        super(itemView);
        checkbox = itemView.findViewById(R.id.checkboxItem_checkbox);
        layout = itemView.findViewById(R.id.checkboxItem_layout);

    }
}
