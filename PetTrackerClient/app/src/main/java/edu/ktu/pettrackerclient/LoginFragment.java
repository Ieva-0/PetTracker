package edu.ktu.pettrackerclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
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

import edu.ktu.pettrackerclient.model.JwtResponse;
import edu.ktu.pettrackerclient.model.LoginRequest;
import edu.ktu.pettrackerclient.retrofit.AuthenticationApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    TextView registerInstead;
    EditText username;
    EditText password;
    Button loginBtn;
    RetrofitService retro;
    AuthenticationApi auth_api;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        registerInstead = v.findViewById(R.id.login_register);
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
        password = v.findViewById(R.id.login_password);
        retro = new RetrofitService();
        auth_api = retro.getRetrofit().create(AuthenticationApi.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRequest req = new LoginRequest();
//                req.setUsername(String.valueOf(username.getText()));
//                String pw = bin2hex(getHash(String.valueOf(password.getText())));
                req.setUsername("reg");
                String pw = bin2hex(getHash("123"));
                req.setPassword(pw);
                Log.d("1122", "password " +pw);
                auth_api.login(req)
                        .enqueue(new Callback<JwtResponse>() {
                            @Override
                            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                                Log.d("1122", response.body().toString());
                                editor.putString("tokenType", response.body().getTokenType());
                                editor.putString("accessToken", response.body().getAccessToken()); // Storing string
                                editor.putString("refreshToken", response.body().getRefreshToken()); // Storing string
                                editor.putLong("user_id", response.body().getId());
                                editor.putString("username", response.body().getUsername());
                                editor.putString("email", response.body().getEmail());
//                                editor.putString("role", response.body().getRoles().toString());
                                editor.commit(); // commit changes
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onFailure(Call<JwtResponse> call, Throwable t) {
                                Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                                Log.d("1122", t.toString());
                                Log.d("1122", pw);
                            }
                        });
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