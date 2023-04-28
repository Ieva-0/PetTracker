package edu.ktu.pettrackerclient;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.ktu.pettrackerclient.adapter.PetCheckboxAdapter;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.PetGroupCreateRequest;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.PetGroupApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupCreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private List<Pet> pets;
    private PetCheckboxAdapter adapter;
    private LinearLayoutManager manager;
    Spinner zone_select;
    List<Zone> zones;
    Zone selectedZone;
    EditText name;

    PetGroupCreateRequest editting;

    Switch notif;

    boolean currentNotifsValue;

    List<Pet> oldItems;
    public PetGroupCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Button save;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pet_group_create, container, false);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        oldItems = new ArrayList<>();
        if(getArguments() != null) {
            Long pet_group_id = getArguments().getLong("pet_group_id");
            if(!pet_group_id.equals(0L)) {
                PetGroupApi petgroupapi = retrofitService.getRetrofit().create(PetGroupApi.class);
                petgroupapi.getPetGroupById(token, pet_group_id).enqueue(new Callback<PetGroup>() {
                    @Override
                    public void onResponse(Call<PetGroup> call, Response<PetGroup> response) {
                        if(response.isSuccessful() && response.body() != null) {

                            PetGroup g = response.body();
                            editting = new PetGroupCreateRequest();
                            editting.setGroup_name(g.getName());
                            editting.setGroup_id(g.getId());
                            editting.setNotifications(g.isNotifications());
                            if(g.getFk_zone_id() != null)
                                editting.setZone_id(g.getFk_zone_id());
                            name.setText(g.getName());
                            notif.setChecked(editting.isNotifications());
                            currentNotifsValue = editting.isNotifications();

                            petgroupapi.getPetsForGroup(token, g.getId()).enqueue(new Callback<List<Pet>>() {
                                @Override
                                public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                                    if(response.isSuccessful() && response.body().size() > 0) {
                                        saveOldPets(response.body());

                                        petApi.getAllPetsForUserWithoutPhotos(token, user_id)
                                                .enqueue(new Callback<List<Pet>>() {
                                                    @Override
                                                    public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                                                        saveResults(response.body());
                                                        populateListView(pets);

                                                        Log.d("1122", String.valueOf(oldItems.get(0)));
                                                        Log.d("1122", String.valueOf(pets.get(1)));
                                                        Log.d("1122", String.valueOf(oldItems.get(0).equals(pets.get(1))));

                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<Pet>> call, Throwable t) {
                                                        Toast.makeText(getContext(), "Failed to load pets", Toast.LENGTH_LONG).show();
                                                        Log.d("1122", String.valueOf(t));

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Pet>> call, Throwable t) {
                                    Log.d("1122", "failed pet group create " + String.valueOf(t));

                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(Call<PetGroup> call, Throwable t) {

                    }
                });
            }
        } else {
            editting = new PetGroupCreateRequest();
            petApi.getAllPetsForUserWithoutPhotos(token, user_id)
                    .enqueue(new Callback<List<Pet>>() {
                        @Override
                        public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                            saveResults(response.body());
                            populateListView(pets);
                        }

                        @Override
                        public void onFailure(Call<List<Pet>> call, Throwable t) {
                            Toast.makeText(getContext(), "Failed to load pets", Toast.LENGTH_LONG).show();
                            Log.d("1122", String.valueOf(t));

                        }
                    });
        }

        recyclerView = v.findViewById(R.id.groupCreate_recycler);
        save = v.findViewById(R.id.petGroupCreate_btn);
        name = v.findViewById(R.id.petGroupCreate_name);
        zone_select = v.findViewById(R.id.deviceEdit_zonePicker);
        zone_select.setOnItemSelectedListener(this);
        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        notif = v.findViewById(R.id.petGroupCreate_enableNotifications);
        currentNotifsValue = false;
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentNotifsValue = !currentNotifsValue;
            }
        });
//        petApi.getAllPetsForUserWithoutPhotos(token, user_id)
//                .enqueue(new Callback<List<Pet>>() {
//                    @Override
//                    public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
//                        saveResults(response.body());
//                        populateListView(pets);
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Pet>> call, Throwable t) {
//                        Toast.makeText(getContext(), "Failed to load pets", Toast.LENGTH_LONG).show();
//                        Log.d("1122", String.valueOf(t));
//
//                    }
//                });

        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);
        zoneApi.getAllZonesForUser(token, user_id)
                .enqueue(new Callback<List<Zone>>() {
                    @Override
                    public void onResponse(Call<List<Zone>> call, Response<List<Zone>> response) {
                        List<String> zone_names = new ArrayList<>();
                        zones = new ArrayList<>();
                        zone_names.add("");
                        response.body().forEach(o ->  {
                            zone_names.add(o.getName());
                            zones.add(o);
                        });
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, zone_names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        zone_select.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Zone>> call, Throwable t) {

                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editting.setPets(adapter.getChecked());
                editting.setGroup_name(name.getText().toString());
                if(selectedZone != null) {
                    editting.setZone_id(selectedZone.getId());
                }
                editting.setNotifications(currentNotifsValue);

                PetGroupApi petgroupapi = retrofitService.getRetrofit().create(PetGroupApi.class);
                petgroupapi.addPetGroup(token, editting).enqueue(new Callback<PetGroup>() {
                    @Override
                    public void onResponse(Call<PetGroup> call, Response<PetGroup> response) {
                        Toast.makeText(getContext(), "successfully created group", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_petGroupCreateFragment_to_drawerNav_petGroupListFragment);

                    }

                    @Override
                    public void onFailure(Call<PetGroup> call, Throwable t) {
                        Toast.makeText(getContext(), "failed to create group", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        return v;
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("1122", "selected: " + adapterView.getItemAtPosition(i));
        if(adapterView.getItemAtPosition(i) != "") {
            zones.forEach(o -> {
                if(o.getName() == adapterView.getItemAtPosition(i))
                    selectedZone = o;
            });
        } else {
            selectedZone = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveOldPets(List<Pet> list) { this.oldItems = list; }
    private void saveResults(List<Pet> list) {
        this.pets = list;
    }

    private void populateListView(List<Pet> list) {
        adapter = new PetCheckboxAdapter(list, getContext(), oldItems);
        recyclerView.setAdapter(adapter);
    }
}