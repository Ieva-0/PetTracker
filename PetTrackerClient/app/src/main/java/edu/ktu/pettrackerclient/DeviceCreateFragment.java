package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceCreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeviceCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceCreateFragment newInstance(String param1, String param2) {
        DeviceCreateFragment fragment = new DeviceCreateFragment();
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
    Spinner zone_select;
    Button save_device;
    EditText device_name;
    EditText device_password;
    List<Zone> zones;
    Zone selectedZone;
    Button cancel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_create, container, false);
        zone_select = v.findViewById(R.id.deviceCreate_zonePicker);
        zone_select.setOnItemSelectedListener(this);

        save_device = v.findViewById(R.id.deviceCreate_btn);
        cancel = v.findViewById(R.id.deviceCreate_cancel);
        device_name = v.findViewById(R.id.deviceCreate_name);
        device_password = v.findViewById(R.id.deviceCreate_password);
//        device_name.setError("uwu");
        RetrofitService retrofitService = new RetrofitService();
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        zoneApi.getAll(token, user_id)
                .enqueue(new Callback<List<Zone>>() {
                    @Override
                    public void onResponse(Call<List<Zone>> call, Response<List<Zone>> response) {
                        List<String> zone_names = new ArrayList<>();
                        zones = new ArrayList<>();
                        zone_names.add("");
                        response.body().forEach(o ->  {
                            zone_names.add(o.getName());
                            zones.add(o);
                        });
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, zone_names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        zone_select.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Zone>> call, Throwable t) {

                    }
                });
        save_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Device device = new Device();
                device.setName(device_name.getText().toString());
                device.setPassword(bin2hex(getHash(device_password.getText().toString())));
                device.setFk_user_id(user_id);
                if(selectedZone != null)
                    device.setFk_zone_id(selectedZone.getId());
                DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
                deviceApi.addDevice(token, device)
                        .enqueue(new Callback<Device>() {
                            @Override
                            public void onResponse(Call<Device> call, Response<Device> response) {
                                Toast.makeText(getContext(), "successfully created device", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_deviceCreateFragment_to_drawerNav_deviceList);

                            }

                            @Override
                            public void onFailure(Call<Device> call, Throwable t) {
                                Toast.makeText(getContext(), "failed to create device", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_deviceCreateFragment_to_drawerNav_deviceList);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("1122", "selected: " + adapterView.getItemAtPosition(i));
        if(adapterView.getItemAtPosition(i) != "") {
            zones.forEach(o -> {
                if(o.getName() == adapterView.getItemAtPosition(i))
                    selectedZone = o;
            });
        } else {
            selectedZone = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }
}