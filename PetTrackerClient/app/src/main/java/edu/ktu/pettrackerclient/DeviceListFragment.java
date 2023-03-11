package edu.ktu.pettrackerclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.adapter.DeviceAdapter;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private List<Device> devices;
    private DeviceAdapter adapter;
    private LinearLayoutManager manager;
    public DeviceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceListFragment newInstance(String param1, String param2) {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        recyclerView = v.findViewById(R.id.device_list);

        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        Log.d("1122", token);
        deviceApi.getAllDevicesForUser(token, user_id)
                .enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load devices", Toast.LENGTH_LONG).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        FloatingActionButton fab = v.findViewById(R.id.fab_addDevice);
        fab.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_drawerNav_deviceList_to_deviceCreateFragment);

        });
        EditText searchTerm = v.findViewById(R.id.deviceSearch_textfield);
        Button b = v.findViewById(R.id.deviceSearch_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Device> filtered = new ArrayList<>();
                devices.forEach(val -> {
                    if(val.getName().contains(searchTerm.getText())) {
                        filtered.add(val);
                    }
                });
                adapter.setItems(filtered);
                /*Log.d("1122", String.valueOf(filtered));
                for(int i = 0; i < filtered.size(); i++){
                    adapter.notifyItemChanged(i);
                }
                recyclerView.setLayoutManager(manager);*/
                adapter.notifyDataSetChanged();

            }
        });

        return v;
    }

    private void saveResults(List<Device> list) {
        this.devices = list;
    }

    private void populateListView(List<Device> list) {
        adapter = new DeviceAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
    }
    public MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = activity;
    }
}