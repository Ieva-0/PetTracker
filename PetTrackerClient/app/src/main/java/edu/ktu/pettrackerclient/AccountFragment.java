package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.ktu.pettrackerclient.model.User;
import edu.ktu.pettrackerclient.retrofit.AuthenticationApi;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Button changePw;
    Button deleteAcc;
    EditText oldPw;
    EditText newPw;
    EditText confirmNewPw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        String password = pref.getString("password", null);
        changePw = v.findViewById(R.id.account_changePwBtn);
        deleteAcc = v.findViewById(R.id.account_delBtn);
        oldPw = v.findViewById(R.id.account_oldPw);
        newPw = v.findViewById(R.id.account_newPw);
        confirmNewPw = v.findViewById(R.id.account_confirmNewPw);

        RetrofitService retrofitService = new RetrofitService();
        AuthenticationApi authenticationApi = retrofitService.getRetrofit().create(AuthenticationApi.class);

        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = bin2hex(getHash(oldPw.getText().toString()));

                if(oldPass.equals(password)) {
                    String newPass = newPw.getText().toString();
                    String newPass_confirm = confirmNewPw.getText().toString();
                    if(newPass.equals(newPass_confirm)) {
                        User u = new User();
                        u.setUsername(pref.getString("username", null));
                        u.setId(pref.getLong("user_id", 0));
                        u.setEmail(pref.getString("email", null));
                        u.setPassword(bin2hex(getHash(newPass)));
                        authenticationApi.changePassword(token, u)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("1122", "successful");
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("1122", t.toString()  );

                                    }
                                });
                    } else {
                        Log.d("1122", "new passwords dont match");
                    }
                } else {
                    Log.d("1122", "wrong old password");
                }
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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