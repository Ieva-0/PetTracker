package edu.ktu.pettrackerclient.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class DeviceHolder extends RecyclerView.ViewHolder {

    TextView name, id;
    Button edit, delete;
    ConstraintLayout layout;
    public DeviceHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.deviceItem_name);
        id = itemView.findViewById(R.id.deviceItem_identifier);
        edit = itemView.findViewById(R.id.deviceItem_edit);
//        delete = itemView.findViewById(R.id.deviceItem_delete);
        layout = itemView.findViewById(R.id.deviceItem_layout);
    }
}
