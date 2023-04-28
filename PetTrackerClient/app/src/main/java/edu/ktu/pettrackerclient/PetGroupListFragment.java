package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.adapter.PetAdapter;
import edu.ktu.pettrackerclient.adapter.PetGroupAdapter;
import edu.ktu.pettrackerclient.adapter.WrapContentLinearLayoutManager;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.PetGroupApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PetGroup> petgroups;
    private PetGroupAdapter adapter;
    private LinearLayoutManager manager;
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
        petGroupApi.getAllPetGroupsForUser(token, user_id)
                .enqueue(new Callback<List<PetGroup>>() {
                    @Override
                    public void onResponse(Call<List<PetGroup>> call, Response<List<PetGroup>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<PetGroup>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load pets", Toast.LENGTH_LONG).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });
        FloatingActionButton fab = v.findViewById(R.id.fab_addPetGroup);
        fab.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_drawerNav_petGroupListFragment_to_petGroupCreateFragment);

        });
//        EditText searchTerm = v.findViewById(R.id.petSearch_textfield);
//        Button b = v.findViewById(R.id.petSearch_button);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                List<Pet> filtered = new ArrayList<>();
//                petgroups.forEach(val -> {
//                    if(val.getName().contains(searchTerm.getText())) {
//                        filtered.add(val);
//                    }
//                });
//                adapter.setItems(filtered);
//                /*Log.d("1122", String.valueOf(filtered));
//                for(int i = 0; i < filtered.size(); i++){
//                    adapter.notifyItemChanged(i);
//                }
//                recyclerView.setLayoutManager(manager);*/
//                adapter.notifyDataSetChanged();
//
//            }
//        });
        return v;
    }

    private void saveResults(List<PetGroup> list) {
        this.petgroups = list;
    }

    private void populateListView(List<PetGroup> list) {
        adapter = new PetGroupAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
    }
}