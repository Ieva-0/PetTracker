package edu.ktu.pettrackerclient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ktu.pettrackerclient.BottomSheet;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.MyMethod;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZonePointAdapter extends RecyclerView.Adapter<ZonePointHolder> {
    private List<ZonePoint> zonePointList;
    private Context ctx;

    BottomSheet sheet;
    public ZonePointAdapter(List<ZonePoint> zonePointList, Context ctx, BottomSheet sheet) {
        this.zonePointList = zonePointList;
        this.ctx = ctx;
        this.sheet = sheet;
    }

    @NonNull
    @Override
    public ZonePointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_point_item, parent, false);

        return new ZonePointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZonePointHolder holder, int position) {
        ZonePoint zonePoint = zonePointList.get(position);
        holder.name.setText(String.valueOf(zonePoint.getLongitude()));
        holder.id.setText(String.valueOf(zonePoint.getList_index() + 1));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("device_id", device.getId());
//                Navigation.findNavController(view).navigate(R.id.list_to_gps, bundle);
//                SharedPreferences pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putLong("zone_id", zone.getId()); // Storing string
//                editor.commit(); // commit changes
//                Intent intent = new Intent(ctx, GpsActivity.class);
//                intent.putExtra("device_id", device.getId());
//                ctx.startActivity(intent);
                Toast.makeText(ctx, "clicked zone" + zonePoint.getList_index(), Toast.LENGTH_SHORT).show();
            }
        });
        RetrofitService retrofitService = new RetrofitService();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Are you sure you'd like to delete this zone?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sheet.executeRemove(holder.getAdapterPosition());
                        zonePointList.remove(holder.getAdapterPosition());
//                        notifyItemRangeChanged(holder.getAdapterPosition(), zonePointList.size());
                        notifyItemRangeRemoved(0, zonePointList.size()+1);
                        notifyItemRangeInserted(0, zonePointList.size());


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return zonePointList.size();
    }

    public void setItems(List<ZonePoint> items) {
        zonePointList = items;
    }

//    private MyMethod mth;
//
//    public void executeLater(MyMethod mth) {
//        this.mth= mth;
//    }
//
//    public void executeNow() {
//        mth.removePoint(0);
//    }


}