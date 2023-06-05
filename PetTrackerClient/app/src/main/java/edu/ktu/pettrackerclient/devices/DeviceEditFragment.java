package edu.ktu.pettrackerclient.devices;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.zones.Zone;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceEditFragment extends Fragment {

    public DeviceEditFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Spinner zone_select;
    Button save_device;
    Button cancel;
    TextInputLayout device_name;
    TextInputLayout device_oldpassword;
    TextInputLayout device_newpassword;
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
        cancel = v.findViewById(R.id.deviceEdit_cancel);
        device_name = v.findViewById(R.id.deviceEdit_name);

        device_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(device_name.getEditText().getText().length() < 4 || device_name.getEditText().getText().length() > 20)
                    device_name.setError(getResources().getString(R.string.name_requirements));
                else device_name.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        device_oldpassword = v.findViewById(R.id.deviceEdit_currentPassword);

        device_oldpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                device_oldpassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        device_newpassword = v.findViewById(R.id.deviceEdit_newPassword);

        device_newpassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((device_newpassword.getEditText().getText().length() < 8 && device_newpassword.getEditText().getText().length() != 0) || device_newpassword.getEditText().getText().length() > 20)
                    device_newpassword.setError(getResources().getString(R.string.password_requirements));
                else device_newpassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

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
                        device_name.getEditText().setText(device_edit.getName());
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
                if(!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Device device = new Device();
                device.setId(device_edit.getId());
                device.setName(device_name.getEditText().getText().toString());
                device.setPassword(bin2hex(getHash(device_newpassword.getEditText().getText().toString())));
                device.setFk_user_id(user_id);
                DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
                deviceApi.addDevice(token, device)
                        .enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().isSuccessful()) {
                                        Navigation.findNavController(view).navigate(R.id.action_deviceEditFragment_to_drawerNav_deviceList);
                                    }
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MessageResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Failed to edit device. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_deviceEditFragment_to_drawerNav_deviceList);
            }
        });
        return v;
    }

    public boolean allFieldsValid() {
        String oldPass = bin2hex(getHash(device_oldpassword.getEditText().getText().toString()));
        boolean valid = true;
        if (device_name.getError() != null || device_name.getEditText().getText().toString().isEmpty()) {
            device_name.setError(getResources().getString(R.string.name_requirements));
            valid = false;
        }
        if (device_newpassword.getError() != null) {
            device_newpassword.setError(getResources().getString(R.string.password_requirements));
            valid = false;
        }
        if (device_oldpassword.getEditText().getText().toString().isEmpty() || !oldPass.equals(device_edit.getPassword())) {
            device_oldpassword.setError("Incorrect current password.");
            valid = false;
        }
        return valid;
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