package edu.ktu.pettrackerclient.users;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> users;
    private UserAdapter adapter;
    private LinearLayoutManager manager;
    AutoCompleteTextView role_filter;
    List<Role> roles;
    TextInputLayout search;
    private TextView noResults;

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_list, container, false);
        noResults = v.findViewById(R.id.user_noResult);

        recyclerView = v.findViewById(R.id.user_list);
        search = v.findViewById(R.id.userSearch_name);
        role_filter = v.findViewById(R.id.pickRole_auto);
        roles = new ArrayList<>();
        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        Log.d("1122", token);
        userApi.getAllUsersWithRoles(token)
                .enqueue(new Callback<UsersWithRolesResponse>() {
                    @Override
                    public void onResponse(Call<UsersWithRolesResponse> call, Response<UsersWithRolesResponse> response) {
                        List<String> role_names = new ArrayList<>();
                        roles = new ArrayList<>();
                        role_names.add("");
                        response.body().getRoles().forEach(o ->  {
                            role_names.add(o.getName());
                            roles.add(o);
                        });
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, role_names);
                        role_filter.setAdapter(adapter);

                        saveResults(response.body().getUsers());
                        populateListView(response.body().getUsers());
                        if(users.size() > 0) {
                            noResults.setVisibility(View.INVISIBLE);

                        } else {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<UsersWithRolesResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
                        Log.d("1122", String.valueOf(t));
                    }
                });
        TextInputLayout searchTerm = v.findViewById(R.id.userSearch_name);
        searchTerm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter();
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        role_filter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                filter();

            }
        });
        return v;
    }

    private void saveResults(List<User> list) {
        this.users = list;
    }

    private void populateListView(List<User> list) {
        adapter = new UserAdapter(list, getContext(), roles, noResults);
        recyclerView.setAdapter(adapter);
    }

    public void filter() {
        List<User> filtered = new ArrayList<>();
        users.forEach(val -> {
            if((val.getUsername().contains(search.getEditText().getText()) || val.getEmail().contains(search.getEditText().getText()))
                    && (val.getFk_role_id().equals(roleIdFromName(role_filter.getText().toString())) || roleIdFromName(role_filter.getText().toString()).equals(-1))) {
                filtered.add(val);
            }
        });
        adapter.setItems(filtered);
        adapter.notifyDataSetChanged();
        if(filtered.size() > 0) {
            noResults.setVisibility(View.INVISIBLE);

        } else {
            noResults.setVisibility(View.VISIBLE);
        }
    }

    public Integer roleIdFromName(String name) {
        for(Role r : roles) {
            if(r.getName().equals(name)) {
                return r.getId();
            }
        }
        return -1;
    }

}