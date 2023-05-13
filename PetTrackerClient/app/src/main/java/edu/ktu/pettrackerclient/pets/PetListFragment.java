package edu.ktu.pettrackerclient.pets;

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
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PetListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PetWithDetails> pets;
    private PetAdapter adapter;
    private LinearLayoutManager manager;
    private TextView noResults;

    public PetListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pet_list, container, false);
        recyclerView = v.findViewById(R.id.pet_list);
        noResults = v.findViewById(R.id.pet_noResult);

        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);
        Log.d("1122", token);

        petApi.getAllPetsForUserWithDetails(token, user_id)
                .enqueue(new Callback<List<PetWithDetails>>() {
                    @Override
                    public void onResponse(Call<List<PetWithDetails>> call, Response<List<PetWithDetails>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                        Log.d("1122", "pet list " + response.body().toString());
                        if(pets.size() > 0) {
                            noResults.setVisibility(View.INVISIBLE);

                        } else {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PetWithDetails>> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        ImageButton fab = v.findViewById(R.id.fab_addPet);
        fab.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_drawerNav_petList_to_petCreateFragment);

        });
        TextInputLayout searchTerm = v.findViewById(R.id.petSearch_name);
        searchTerm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<PetWithDetails> filtered = new ArrayList<>();
                pets.forEach(val -> {
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
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return v;
    }

    private void saveResults(List<PetWithDetails> list) {
        this.pets = list;
    }

    private void populateListView(List<PetWithDetails> list) {
        adapter = new PetAdapter(list, getContext(), noResults);
        recyclerView.setAdapter(adapter);
    }
}