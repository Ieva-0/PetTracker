package edu.ktu.pettrackerclient.users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.ktu.pettrackerclient.MainActivity;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView loginInstead;
    TextInputLayout username;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout confirmPassword;
    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        username = v.findViewById(R.id.register_username);

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

        email = v.findViewById(R.id.register_email);

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

        password = v.findViewById(R.id.register_password);

        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getEditText().getText().length() < 8 || password.getEditText().getText().length() > 20)
                    password.setError(getResources().getString(R.string.password_requirements));
                else password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        confirmPassword = v.findViewById(R.id.register_confirmPassword);

        confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirmPassword.getEditText().getText().length() < 8 || confirmPassword.getEditText().getText().length() > 20)
                    confirmPassword.setError(getResources().getString(R.string.confirm_password_requirements));
                else confirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        save = v.findViewById(R.id.registerBtn);

        RetrofitService retrofitService = new RetrofitService();
        AuthenticationApi authApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = username.getEditText().getText().toString();
                String em = email.getEditText().getText().toString();
                String pw = password.getEditText().getText().toString();
                SignupRequest req = new SignupRequest();
                req.setPassword(bin2hex(getHash(pw)));
                req.setUsername(name);
                req.setEmail(em);
                Log.d("1122", req.toString());

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.d("1122", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                // Get new FCM registration token
                                String token = task.getResult();
                                req.setFirebase_token(token);
                                // Log and toast
                                Log.d("1122", "got firebase token: " + token);
                                authApi.register(req).enqueue(new Callback<JwtResponse>() {
                                    @Override
                                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                                        if (response.isSuccessful()) {
                                            JwtResponse result = response.body();
                                            if(result.isSuccessful()) {
                                                editor.putString("tokenType", result.getTokenType());
                                                editor.putString("accessToken", result.getAccessToken()); // Storing string
                                                editor.putString("refreshToken", result.getRefreshToken()); // Storing string
                                                editor.putLong("user_id", result.getId());
                                                editor.putString("username", result.getUsername());
                                                editor.putString("email", result.getEmail());
                                                editor.putInt("role", result.getRole());
                                                editor.commit(); // commit changes
                                                Intent i = new Intent(getActivity(), MainActivity.class);
                                                startActivity(i);
                                            }
                                            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        Log.d("1122", String.valueOf(t));
                                    }
                                });
                            }
                        });

            }
        });

        loginInstead = v.findViewById(R.id.register_login);
        loginInstead.setPaintFlags(loginInstead.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);

            }
        });
        return v;
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
            password.setError(getResources().getString(R.string.password_requirements));
            valid = false;
        }
        if (confirmPassword.getError() != null || confirmPassword.getEditText().getText().toString().isEmpty() || !confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) {
            confirmPassword.setError(getResources().getString(R.string.confirm_password_requirements));
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