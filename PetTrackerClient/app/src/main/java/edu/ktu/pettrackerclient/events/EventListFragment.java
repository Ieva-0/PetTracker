package edu.ktu.pettrackerclient.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.checkboxes.PetCheckboxAdapter;
import edu.ktu.pettrackerclient.checkboxes.PetGroupCheckboxAdapter;
import edu.ktu.pettrackerclient.checkboxes.ZoneCheckboxAdapter;
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.pet_groups.PetGroup;
import edu.ktu.pettrackerclient.zones.Zone;
import edu.ktu.pettrackerclient.pets.PetApi;
import edu.ktu.pettrackerclient.pet_groups.PetGroupApi;
import edu.ktu.pettrackerclient.RetrofitService;
import edu.ktu.pettrackerclient.zones.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private TextView emptyText;
    private RecyclerView recyclerView;
    private List<EventWithDetails> events;
    private EventAdapter adapter;
    private LinearLayoutManager manager;

    private Button filters_pets;
    private Button filters_zones;
    private Button filters_groups;

    private List<Pet> pets;
    private List<Pet> picked_pets;
    private List<PetGroup> groups;
    private List<PetGroup> picked_groups;
    private List<Zone> zones;
    private List<Zone> picked_zones;

    private List<Event> filtered;
    public EventListFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picked_pets = new ArrayList<>();
        picked_groups = new ArrayList<>();
        picked_zones = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        emptyText = v.findViewById(R.id.empty_text);
        emptyText.setVisibility(View.INVISIBLE);
        emptyText.bringToFront();

        RetrofitService retrofitService = new RetrofitService();
        EventApi eventApi = retrofitService.getRetrofit().create(EventApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        recyclerView = v.findViewById(R.id.event_list);
        filters_pets = v.findViewById(R.id.eventSearch_pets);
        filters_zones = v.findViewById(R.id.eventSearch_zones);
        filters_groups = v.findViewById(R.id.eventSearch_groups);

        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);

        petApi.getAllPetsForUser(token, user_id).enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                savePets(response.body());
            }
            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) { }
        });

        filters_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Choose pets");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = (View) inflater.inflate(R.layout.custom_alert_dialog, null);

                builder.setView(dialogView);

                RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.dialog_recycler);
                LinearLayoutManager man = new LinearLayoutManager(v.getContext());
                rv.setLayoutManager(man);
                rv.setHasFixedSize(true);
                PetCheckboxAdapter adapter = new PetCheckboxAdapter(pets, getActivity(), picked_pets);
                rv.setAdapter(adapter);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        picked_pets = adapter.getChecked();
                        filterList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);

        zoneApi.getAllZonesForUser(token, user_id).enqueue(new Callback<List<Zone>>() {
            @Override
            public void onResponse(Call<List<Zone>> call, Response<List<Zone>> response) {
                saveZones(response.body());
            }
            @Override
            public void onFailure(Call<List<Zone>> call, Throwable t) { }
        });

        filters_zones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Choose zones");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = (View) inflater.inflate(R.layout.custom_alert_dialog, null);

                builder.setView(dialogView);

                RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.dialog_recycler);
                LinearLayoutManager man = new LinearLayoutManager(v.getContext());
                rv.setLayoutManager(man);
                rv.setHasFixedSize(true);
                ZoneCheckboxAdapter adapter = new ZoneCheckboxAdapter(zones, getActivity(), picked_zones);
                rv.setAdapter(adapter);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        picked_zones = adapter.getChecked();
                        filterList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        PetGroupApi groupApi = retrofitService.getRetrofit().create(PetGroupApi.class);

        groupApi.getAllPetGroupsForUser(token, user_id).enqueue(new Callback<List<PetGroup>>() {
            @Override
            public void onResponse(Call<List<PetGroup>> call, Response<List<PetGroup>> response) {
                saveGroups(response.body());
            }
            @Override
            public void onFailure(Call<List<PetGroup>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.d("1122", String.valueOf(t));
            }
        });

        filters_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Choose pet groups");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = (View) inflater.inflate(R.layout.custom_alert_dialog, null);

                builder.setView(dialogView);

                RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.dialog_recycler);
                LinearLayoutManager man = new LinearLayoutManager(v.getContext());
                rv.setLayoutManager(man);
                rv.setHasFixedSize(true);
                PetGroupCheckboxAdapter adapter = new PetGroupCheckboxAdapter(groups, getActivity(), picked_groups);
                rv.setAdapter(adapter);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        picked_groups = adapter.getChecked();
                        filterList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        manager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        Log.d("1122", token);
        eventApi.getEventsWithDetailsForUser(token, user_id)
                .enqueue(new Callback<List<EventWithDetails>>() {
                    @Override
                    public void onResponse(Call<List<EventWithDetails>> call, Response<List<EventWithDetails>> response) {
                        saveResults(response.body());
                        populateListView(response.body());
                        if(response.body().size()== 0) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("No events exist.");
                        } else {
                            emptyText.setVisibility(View.INVISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<EventWithDetails>> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("1122", String.valueOf(t));

                    }
                });


        return v;
    }

    private void filterList() {
        List<EventWithDetails> newList = new ArrayList<>();
        for(EventWithDetails e : events) {
            if(picked_pets != null && picked_pets.size() != 0) {
                boolean found = false;
                for(Pet p : picked_pets) {
                    if(p.getId().equals(e.getPet().getId())) {
                        found = true;
                    }
                }
                if(!found) {
                    continue;
                }
            }
            if(picked_zones != null && picked_zones.size() != 0) {
                boolean found = false;
                for(Zone z : picked_zones) {
                    if(z.getId().equals(e.getZone().getId())) {
                        found = true;
                    }
                }
                if(!found) {
                    continue;
                }
            }
            if(picked_groups != null && picked_groups.size() != 0) {
                boolean found = false;
                for(PetGroup g : picked_groups) {
                    if(e.getPet_group() != null && e.getPet_group().getId() != null && g.getId().equals(e.getPet_group().getId())) {
                        found = true;
                    }
                }
                if(!found) {
                    continue;
                }
            }
            newList.add(e);
        }
        if(newList.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No events match the criteria.");
        } else {
            emptyText.setVisibility(View.INVISIBLE);
        }
        populateListView(newList);
    }


    private void savePets(List<Pet> list) {
        this.pets = list;
    }
    private void saveZones(List<Zone> list) {
        this.zones = list;
    }
    private void saveGroups(List<PetGroup> list) {
        this.groups = list;
    }
    private void saveResults(List<EventWithDetails> list) {
        this.events = list;
    }

    private void populateListView(List<EventWithDetails> list) {
        adapter = new EventAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
    }
}