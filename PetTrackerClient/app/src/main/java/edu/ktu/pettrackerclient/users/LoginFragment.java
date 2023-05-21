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
import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    TextView registerInstead;
    TextInputLayout username;
    TextInputLayout password;
    Button loginBtn;
    RetrofitService retro;
    AuthenticationApi auth_api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        registerInstead = v.findViewById(R.id.login_register);
        registerInstead.setPaintFlags(registerInstead.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        registerInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);

            }
        });

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        loginBtn = v.findViewById(R.id.loginBtn);
        username = v.findViewById(R.id.login_username);

        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (username.getEditText().getText().length() == 0)
                    username.setError("Field cannot be empty.");
                else username.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password = v.findViewById(R.id.login_password);

        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getEditText().getText().length() == 0)
                    password.setError("Field cannot be empty.");
                else password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        retro = new RetrofitService();
        auth_api = retro.getRetrofit().create(AuthenticationApi.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginRequest req = new LoginRequest();
                req.setUsername(String.valueOf(username.getEditText().getText()));
                String pw = bin2hex(getHash(password.getEditText().getText().toString()));
                req.setPassword(pw);

                auth_api.login(req)
                        .enqueue(new Callback<JwtResponse>() {
                            @Override
                            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                                if (response.isSuccessful()) {
                                    JwtResponse result = response.body();
                                    if(result.isSuccessful()) {
                                        if(result.getFirebase_token() != null) {
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
                                                            // Log and toast
                                                            Log.d("1122", "got firebase token after login: " + token);
                                                            if(!result.getFirebase_token().equals(token)) {
                                                                User user = new User();
                                                                user.setFirebase_token(token);
                                                                user.setId(result.getId());
                                                                auth_api.firebase_token(user).enqueue(new Callback<MessageResponse>() {
                                                                    @Override
                                                                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                                                        Log.d("1122",response.body().getMessage());
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<MessageResponse> call, Throwable t) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                        }
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
        return v;
    }

    public boolean allFieldsValid() {
        boolean valid = true;
        if (username.getEditText().getText().toString().isEmpty()) {
            username.setError("Field cannot be empty.");
            valid = false;
        }

        if (password.getEditText().getText().toString().isEmpty()) {
            password.setError("Field cannot be empty.");
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