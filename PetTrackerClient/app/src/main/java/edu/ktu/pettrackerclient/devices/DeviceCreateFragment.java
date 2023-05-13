package edu.ktu.pettrackerclient.devices;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
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

    TextInputLayout device_name, device_password;
    Button cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_create, container, false);

        save_device = v.findViewById(R.id.deviceCreate_btn);
        cancel = v.findViewById(R.id.deviceCreate_cancel);
        device_name = v.findViewById(R.id.deviceCreate_name);
        device_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (device_name.getEditText().getText().length() < 4 || device_name.getEditText().getText().length() > 20)
                    device_name.setError(getResources().getString(R.string.name_requirements));
                else device_name.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        device_password = v.findViewById(R.id.deviceCreate_password);
        device_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (device_password.getEditText().getText().length() < 8 || device_password.getEditText().getText().length() > 20)
                    device_password.setError(getResources().getString(R.string.password_requirements));
                else device_password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        RetrofitService retrofitService = new RetrofitService();
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        save_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Device device = new Device();
                device.setName(device_name.getEditText().getText().toString());
                device.setPassword(bin2hex(getHash(device_password.getEditText().getText().toString())));
                device.setFk_user_id(user_id);
                DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
                deviceApi.addDevice(token, device)
                        .enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().isSuccessful()) {
                                        Navigation.findNavController(view).navigate(R.id.action_deviceCreateFragment_to_drawerNav_deviceList);
                                    }
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MessageResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Failed to create device. Please try again.", Toast.LENGTH_SHORT).show();
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

    public boolean allFieldsValid() {
        boolean valid = true;
        if (device_name.getError() != null || device_name.getEditText().getText().toString().isEmpty()) {
            device_name.setError(getResources().getString(R.string.name_requirements));
            valid = false;
        }

        if (device_password.getError() != null || device_password.getEditText().getText().toString().isEmpty()) {
            device_password.setError(getResources().getString(R.string.password_requirements));
            valid = false;
        }
        return valid;
    }

    public byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}