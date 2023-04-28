package edu.ktu.pettrackerclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import edu.ktu.pettrackerclient.adapter.ZonePointAdapter;
import edu.ktu.pettrackerclient.model.ZonePoint;
import edu.ktu.pettrackerclient.retrofit.ZonePointDelegation;

public class BottomSheet extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    ZonePointAdapter adapter;
    private LinearLayoutManager manager;
    private List<ZonePoint> points;

    public BottomSheet(List<ZonePoint> items) {
        this.points = items;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.points_list_layout,
                container, false);

        recyclerView = v.findViewById(R.id.zonePointList);
        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        Button algo_button = v.findViewById(R.id.dismissbtn);
        populateListView(points);

        algo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                Toast.makeText(getActivity(),
//                                "byebye:(", Toast.LENGTH_SHORT)
//                        .show();
                dismiss();
            }
        });


        return v;
    }
    private void populateListView(List<ZonePoint> list) {
        adapter = new ZonePointAdapter(list, getContext(), this);
//        adapter.setCommunicator((ZonePointAdapter.Communicator) getActivity());
        recyclerView.setAdapter(adapter);

    }
    private ZonePointDelegation remove;
    private ZonePointDelegation update;
    public void setRemove(ZonePointDelegation mth) {
        this.remove = mth;
    }
    public void executeRemove(int index) {
        remove.myMethod(index);
    }
    public void setUpdate(ZonePointDelegation mth) {
        this.update = mth;
    }
    public void executeUpdate(int index) {
        update.myMethod(index);
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        Bundle result = new Bundle();
        result.putString("bundleKey", "result");
        getParentFragmentManager().setFragmentResult("requestKey", result);
//        Toast.makeText(getContext(), "onCancel fired", Toast.LENGTH_SHORT).show();
        executeUpdate(0);
        Log.d("1122", "oncancel fired");
    }
}