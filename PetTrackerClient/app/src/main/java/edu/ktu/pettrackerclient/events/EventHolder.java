package edu.ktu.pettrackerclient.events;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.ktu.pettrackerclient.R;

public class EventHolder extends RecyclerView.ViewHolder {
    TextView info, date;
    ConstraintLayout layout;
    public EventHolder(@NonNull View itemView) {
        super(itemView);
        info = itemView.findViewById(R.id.eventList_info);
        date = itemView.findViewById(R.id.eventList_date);
        layout = itemView.findViewById(R.id.petItem_layout);

    }
}
