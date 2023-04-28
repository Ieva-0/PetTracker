package edu.ktu.pettrackerclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.ktu.pettrackerclient.model.JwtResponse;
import edu.ktu.pettrackerclient.model.SignupRequest;
import edu.ktu.pettrackerclient.retrofit.AuthenticationApi;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
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
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;
    Button save;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        username = v.findViewById(R.id.register_username);
        email = v.findViewById(R.id.register_email);
        password = v.findViewById(R.id.register_password);
        confirmPassword = v.findViewById(R.id.register_confirmPassword);
        save = v.findViewById(R.id.registerBtn);

        RetrofitService retrofitService = new RetrofitService();
        AuthenticationApi authApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String em = username.getText().toString();
                String pw = username.getText().toString();
                String cpw = username.getText().toString();
                if(!Objects.equals(cpw, pw) || name.isEmpty() || em.isEmpty() || pw.isEmpty() || cpw.isEmpty()) {
                    Toast.makeText(getContext(), "wrong info entered", Toast.LENGTH_SHORT).show();
                }
                else {
                    SignupRequest req = new SignupRequest();
                    req.setPassword(bin2hex(getHash(pw)));
                    req.setUsername(name);
                    req.setEmail(em);
                    Set<String> roles = new HashSet<String>();
                    roles.add("admin");
                    req.setRole(roles);
                    authApi.register(req).enqueue(new Callback<JwtResponse>() {
                        @Override
                        public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                            if(response.isSuccessful()) {
                                Toast.makeText(getContext(), "successfully registered", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<JwtResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "failed to register", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
        
        loginInstead = v.findViewById(R.id.register_login);
        loginInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);

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