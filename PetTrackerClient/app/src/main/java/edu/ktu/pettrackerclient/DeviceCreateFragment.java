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
public class DeviceCreateFragment extends Fragment {

    public DeviceCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    Button save_device;
    EditText device_name;
    EditText device_password;
    Button cancel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_create, container, false);

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
        save_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Device device = new Device();
                device.setName(device_name.getText().toString());
                device.setPassword(bin2hex(getHash(device_password.getText().toString())));
                device.setFk_user_id(user_id);
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
        return v;
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