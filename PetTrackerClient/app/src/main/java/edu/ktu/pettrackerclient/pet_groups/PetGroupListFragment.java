package edu.ktu.pettrackerclient.pet_groups;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.WrapContentLinearLayoutManager;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PetGroupWithDetails> petgroups;
    private PetGroupAdapter adapter;
    private LinearLayoutManager manager;
    private TextView noResults;
    public PetGroupListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_pet_group_list, container, false);
        noResults = v.findViewById(R.id.petGroup_noResult);

        recyclerView = v.findViewById(R.id.petGroup_list);

        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        RetrofitService retrofitService = new RetrofitService();
        PetGroupApi petGroupApi = retrofitService.getRetrofit().create(PetGroupApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        Log.d("1122", token);
        petGroupApi.getAllPetGroupsWithDetailsForUser(token, user_id)
                .enqueue(new Callback<List<PetGroupWithDetails>>() {
                    @Override
                    public void onResponse(Call<List<PetGroupWithDetails>> call, Response<List<PetGroupWithDetails>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                        if(petgroups.size() > 0) {
                            noResults.setVisibility(View.INVISIBLE);

                        } else {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PetGroupWithDetails>> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        ImageButton fab = v.findViewById(R.id.fab_addPetGroup);
        fab.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_drawerNav_petGroupListFragment_to_petGroupCreateFragment);

        });
        TextInputLayout searchTerm = v.findViewById(R.id.petGroupSearch_name);
        searchTerm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<PetGroupWithDetails> filtered = new ArrayList<>();
                if(petgroups != null) {
                    petgroups.forEach(val -> {
                        if(val.getName().contains(searchTerm.getEditText().getText())) {
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
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return v;
    }
    private void saveResults(List<PetGroupWithDetails> list) {
        this.petgroups = list;
    }

    private void populateListView(List<PetGroupWithDetails> list) {
        adapter = new PetGroupAdapter(list, getContext(), noResults);
        recyclerView.setAdapter(adapter);
    }
}