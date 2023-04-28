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

public class DeviceEditFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public DeviceEditFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Spinner zone_select;
    Button save_device;
    EditText device_name;
    EditText device_oldpassword;
    EditText device_newpassword;
    EditText device_confirmnewpassword;
    List<Zone> zones;
    Zone selectedZone;
    Device device_edit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_edit, container, false);
        Long device_id = getArguments().getLong("device_id");


        save_device = v.findViewById(R.id.deviceEdit_btn);
        device_name = v.findViewById(R.id.deviceEdit_name);
        device_oldpassword = v.findViewById(R.id.deviceEdit_oldPassword);
        device_newpassword = v.findViewById(R.id.deviceEdit_newPassword);
        device_confirmnewpassword = v.findViewById(R.id.deviceEdit_confirmNewPassword);

        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);

        deviceApi.getDeviceById(token, device_id)
                .enqueue(new Callback<Device>() {
                    @Override
                    public void onResponse(Call<Device> call, Response<Device> response) {
                        Log.d("1122", String.valueOf(response.body()));
                        device_edit = response.body();
                        Log.d("1122", device_edit.toString());
                        if(device_edit.getId() == null) {
                            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).navigate(R.id.action_deviceCreateFragment_to_drawerNav_deviceList);
                        }
                        device_name.setText(device_edit.getName());
//                        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
//                        zoneApi.getAll(token, user_id)
//                                .enqueue(new Callback<List<Zone>>() {
//                                    @Override
//                                    public void onResponse(Call<List<Zone>> call, Response<List<Zone>> response) {
//                                        List<String> zone_names = new ArrayList<>();
//                                        zones = new ArrayList<>();
//                                        zone_names.add("");
//                                        Log.d("1122", "fk zone" + String.valueOf(device_edit.getFk_zone_id()));
//                                        Log.d("1122", "fk zone" + response.body());
//
//                                        int position = -1;
//                                        for (Zone o : response.body()) {
//                                            zone_names.add(o.getName());
//                                            zones.add(o);
//                                            if (device_edit.getFk_zone_id() != null) {
//                                                if (o.getId().equals(device_edit.getFk_zone_id())) {
//                                                    position = zones.indexOf(o);
//                                                }
//                                            }
//                                        }
//                                        Log.d("1122", "position" + String.valueOf(position));
//                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, zone_names);
//                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                        zone_select.setAdapter(adapter);
//                                        if(position >= 0)
//                                            zone_select.setSelection(position+1);
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<List<Zone>> call, Throwable t) {
//
//                                    }
//                                });

                    }

                    @Override
                    public void onFailure(Call<Device> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load devices", Toast.LENGTH_LONG).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        save_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPasswords()) {
                    Toast.makeText(getContext(), "wrong password(s)", Toast.LENGTH_SHORT).show();
                    return;
                }
                Device device = new Device();
                device.setId(device_edit.getId());
                device.setName(device_name.getText().toString());
                device.setPassword(bin2hex(getHash(device_newpassword.getText().toString())));
                device.setFk_user_id(user_id);
//                if(selectedZone != null)
//                    device.setFk_zone_id(selectedZone.getId());
                DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
                deviceApi.addDevice(token, device)
                        .enqueue(new Callback<Device>() {
                            @Override
                            public void onResponse(Call<Device> call, Response<Device> response) {
                                Toast.makeText(getContext(), "successfully modified device", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_deviceEditFragment_to_drawerNav_deviceList);

                            }

                            @Override
                            public void onFailure(Call<Device> call, Throwable t) {
                                Toast.makeText(getContext(), "failed to edit device", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return v;
    }

    public boolean checkPasswords() {
        String oldPass = bin2hex(getHash(device_oldpassword.getText().toString()));

        if(oldPass.equals(device_edit.getPassword())) {
            String newPass = device_newpassword.getText().toString();
            String newPass_confirm = device_confirmnewpassword.getText().toString();
            if(newPass.isEmpty() && newPass_confirm.isEmpty()) {
                return true;
            } else if(newPass.equals(newPass_confirm)) {
                return true;
            } else {
                Log.d("1122", device_newpassword.getText().toString());
                Log.d("1122", device_confirmnewpassword.getText().toString());
                return false;
            }
        } else {
            Log.d("1122", oldPass);
            Log.d("1122", device_edit.getPassword());
            return false;
        }
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