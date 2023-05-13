package edu.ktu.pettrackerclient.users;

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
import edu.ktu.pettrackerclient.zones.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    TextInputLayout username;
    TextInputLayout email;
    TextInputLayout password;
    Button save, cancel;
    User editting;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);

        username = v.findViewById(R.id.editProfile_username);
        password = v.findViewById(R.id.editProfile_password);
        email = v.findViewById(R.id.editProfile_email);

        userApi.getUser(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    editting = response.body();
                    username.getEditText().setText(editting.getUsername());
                    email.getEditText().setText(editting.getEmail());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        save = v.findViewById(R.id.editProfile_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                editting.setUsername(username.getEditText().getText().toString());
                editting.setEmail(email.getEditText().getText().toString());
                editting.setPassword(bin2hex(getHash(password.getEditText().getText().toString())));

                userApi.editProfile(token, editting).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if (response.isSuccessful()) {
                            if(response.body().isSuccessful()) {
                                Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_drawerNav_accountFragment);
                            }
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {

                    }
                });
            }
        });

        cancel = v.findViewById(R.id.editProfile_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_drawerNav_accountFragment);
            }
        });
        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (username.getEditText().getText().length() < 4 || username.getEditText().getText().length() > 20)
                    username.setError(getResources().getString(R.string.name_requirements));
                else username.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (email.getEditText().getText().length() < 8 || email.getEditText().getText().length() > 30 || !email.getEditText().getText().toString().contains("@"))
                    email.setError(getResources().getString(R.string.email_requirements));
                else email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getEditText().getText().length() ==0)
                    password.setError("");
                else password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        return v;
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

    public boolean allFieldsValid() {
        boolean valid = true;
        if (username.getError() != null || username.getEditText().getText().toString().isEmpty()) {
            username.setError(getResources().getString(R.string.name_requirements));
            valid = false;
        }

        if (email.getError() != null || email.getEditText().getText().toString().isEmpty() || !email.getEditText().getText().toString().contains("@")) {
            email.setError(getResources().getString(R.string.email_requirements));
            valid = false;
        }
        if (password.getError() != null || password.getEditText().getText().toString().isEmpty()) {
            password.setError("");
            valid = false;
        }
        return valid;
    }
}