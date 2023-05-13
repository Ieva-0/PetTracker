package edu.ktu.pettrackerclient.users;

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
public class ChangePasswordFragment extends Fragment {

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextInputLayout oldPw;
    TextInputLayout newPw;
    TextInputLayout confirmNewPw;
    Button changePw, cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        oldPw = v.findViewById(R.id.account_oldPassword);
        newPw = v.findViewById(R.id.account_newPassword);
        confirmNewPw = v.findViewById(R.id.account_confirmPassword);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);


        oldPw.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (oldPw.getEditText().getText().length() < 8 || oldPw.getEditText().getText().length() > 20)
                    oldPw.setError(getResources().getString(R.string.password_requirements));
                else oldPw.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newPw.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (newPw.getEditText().getText().length() < 8 || newPw.getEditText().getText().length() > 20)
                    newPw.setError(getResources().getString(R.string.confirm_password_requirements));
                else newPw.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        confirmNewPw.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirmNewPw.getEditText().getText().length() < 8 || confirmNewPw.getEditText().getText().length() > 20
                        || !confirmNewPw.getEditText().getText().toString().equals(newPw.getEditText().getText().toString()))
                    confirmNewPw.setError(getResources().getString(R.string.confirm_password_requirements));
                else confirmNewPw.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        changePw = v.findViewById(R.id.changePw_btn);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePasswordRequest req = new ChangePasswordRequest();
                req.setOld_pw(bin2hex(getHash(oldPw.getEditText().getText().toString())));
                req.setNew_pw(bin2hex(getHash(newPw.getEditText().getText().toString())));

                userApi.changePassword(token, req)
                        .enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                if (response.isSuccessful()) {
                                    if(response.body().isSuccessful()) {
                                        Navigation.findNavController(view).navigate(R.id.action_changePasswordFragment_to_drawerNav_accountFragment);
                                    }
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MessageResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Failed to change password.", Toast.LENGTH_SHORT).show();
                                Log.d("1122", String.valueOf(t));

                            }
                        });
            }
        });
        cancel = v.findViewById(R.id.changePw_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_changePasswordFragment_to_drawerNav_accountFragment);
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

        if (oldPw.getError() != null || oldPw.getEditText().getText().toString().isEmpty()) {
            oldPw.setError(getResources().getString(R.string.password_requirements));
            valid = false;
        }
        if (newPw.getError() != null || newPw.getEditText().getText().toString().isEmpty()) {
            newPw.setError(getResources().getString(R.string.password_requirements));
            valid = false;
        }
        if (confirmNewPw.getError() != null || confirmNewPw.getEditText().getText().toString().isEmpty() || !confirmNewPw.getEditText().getText().toString().equals(newPw.getEditText().getText().toString())) {
            confirmNewPw.setError(getResources().getString(R.string.confirm_password_requirements));
            valid = false;
        }
        return valid;
    }
}