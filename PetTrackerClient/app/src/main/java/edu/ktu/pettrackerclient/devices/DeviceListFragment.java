package edu.ktu.pettrackerclient.devices;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceListFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<DeviceWithDetails> devices;
    private DeviceAdapter adapter;
    private LinearLayoutManager manager;
    private TextView noResults;

    public DeviceListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        noResults = v.findViewById(R.id.device_noResult);

        recyclerView = v.findViewById(R.id.device_list);

        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        deviceApi.getAllDevicesWithDetailsForUser(token, user_id)
                .enqueue(new Callback<List<DeviceWithDetails>>() {
                    @Override
                    public void onResponse(Call<List<DeviceWithDetails>> call, Response<List<DeviceWithDetails>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                        if(devices.size() > 0) {
                            noResults.setVisibility(View.INVISIBLE);

                        } else {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DeviceWithDetails>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load devices", Toast.LENGTH_LONG).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        ImageButton fab = v.findViewById(R.id.fab_addDevice);
        fab.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_drawerNav_deviceList_to_deviceCreateFragment);

        });

        TextInputLayout searchTerm = v.findViewById(R.id.deviceSearch_name);
        searchTerm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<DeviceWithDetails> filtered = new ArrayList<>();
                if(devices != null) {
                    devices.forEach(val -> {
                        if(val.getName().contains(searchTerm.getEditText().getText())) {
                            filtered.add(val);
                        }
                    });
                }

                adapter.setItems(filtered);
                adapter.notifyDataSetChanged();
                if(filtered.size() > 0) {
                    noResults.setVisibility(View.INVISIBLE);

                } else {
                    noResults.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        return v;
    }

    private void saveResults(List<DeviceWithDetails> list) {
        this.devices = list;
    }

    private void populateListView(List<DeviceWithDetails> list) {
        adapter = new DeviceAdapter(list, getContext(), noResults);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}