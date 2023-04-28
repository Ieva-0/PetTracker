package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class ZoneCheckboxHolder extends RecyclerView.ViewHolder {
    CheckBox zone;
    ConstraintLayout layout;
    public ZoneCheckboxHolder(@NonNull View itemView) {
        super(itemView);
        zone = itemView.findViewById(R.id.checkboxItem_checkbox);
        layout = itemView.findViewById(R.id.groupCreate_petItemLayout);

    }
}
