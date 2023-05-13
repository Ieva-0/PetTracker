package edu.ktu.pettrackerclient.pet_groups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.checkboxes.PetCheckboxAdapter;
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.zones.Zone;
import edu.ktu.pettrackerclient.pets.PetApi;
import edu.ktu.pettrackerclient.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetGroupCreateFragment extends Fragment {
    private RecyclerView recyclerView;

    private List<Pet> chosen_pets;
    private List<Pet> pets;
    private PetCheckboxAdapter adapter;
    private LinearLayoutManager manager;
    List<Zone> zones;
    TextInputLayout name;
    PetGroupWithDetails editting;
    Switch notif;

    boolean currentNotifsValue;

    Button save;
    Button cancel;
    Button choose_pets;

    TextView petsChosenText;
    MaterialAutoCompleteTextView pick_zone;

    public PetGroupCreateFragment() {
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
        View v = inflater.inflate(R.layout.fragment_pet_group_create, container, false);

        RetrofitService retrofitService = new RetrofitService();
        PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        chosen_pets = new ArrayList<>();
        Long pet_group_id = 0L;
        if (getArguments() != null) {
            pet_group_id = getArguments().getLong("pet_group_id");
        } else {
            editting = new PetGroupWithDetails();
        }
        pick_zone = v.findViewById(R.id.pickZone_auto);

        PetGroupApi petgroupapi = retrofitService.getRetrofit().create(PetGroupApi.class);
        petgroupapi.getPetGroupById(token, pet_group_id).enqueue(new Callback<PetGroupCreateEditResponse>() {
            @Override
            public void onResponse(Call<PetGroupCreateEditResponse> call, Response<PetGroupCreateEditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //petgroup-------------------------

                    PetGroupCreateEditResponse g = response.body();
                    Log.d("1122", g.toString());
                    editting = response.body().getPet_group();
                    if (editting.getId() != null) {
                        name.getEditText().setText(editting.getName().toString());
                        chosen_pets = editting.getPets();
                        petsChosenText.setText(chosen_pets.size() + " pet(s) chosen");
                        notif.setChecked(editting.isNotifications());
                        currentNotifsValue = editting.isNotifications();
                    }

                    // zones-------------------------

                    List<String> zone_names = new ArrayList<>();
                    zones = new ArrayList<>();
                    zone_names.add("");
                    Zone oldZoneObj = null;
                    if (editting.getZone() != null) {
                        oldZoneObj = editting.getZone();
                        zone_names.add(oldZoneObj.getName());
                        zones.add(oldZoneObj);
                    }
                    for (Zone o : response.body().getZones()) {
                        zone_names.add(o.getName());
                        zones.add(o);
                    }

                    Log.d("1122", zone_names.toString());
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, zone_names);
                    pick_zone.setAdapter(adapter2);
                    if (oldZoneObj != null)
                        pick_zone.setText(oldZoneObj.getName(), false);

                    // pets ----------------------------
                    pets = response.body().getPets();
                }
            }

            @Override
            public void onFailure(Call<PetGroupCreateEditResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.d("1122", String.valueOf(t));
            }
        });

        save = v.findViewById(R.id.petGroupCreate_btn);
        cancel = v.findViewById(R.id.petGroupCreate_cancel);
        petsChosenText = v.findViewById(R.id.petsChosen_text);
        name = v.findViewById(R.id.petGroupCreate_name);

        name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (name.getEditText().getText().length() < 4 || name.getEditText().getText().length() > 20)
                    name.setError(getResources().getString(R.string.name_requirements));
                else name.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        manager = new LinearLayoutManager(v.getContext());
        notif = v.findViewById(R.id.petGroupCreate_enableNotifications);
        currentNotifsValue = false;
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentNotifsValue = !currentNotifsValue;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!allFieldsValid()) {
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                PetGroupCreateEditRequest req = new PetGroupCreateEditRequest();
                if (editting != null) {
                    req.setGroup_id(editting.getId());
                }
                req.setPets(chosen_pets);
                req.setGroup_name(name.getEditText().getText().toString());
                if (getZoneId(pick_zone.getText().toString()) > 0)
                    req.setZone_id(getZoneId(pick_zone.getText().toString()));
                req.setNotifications(currentNotifsValue);

                PetGroupApi petgroupapi = retrofitService.getRetrofit().create(PetGroupApi.class);
                petgroupapi.addPetGroup(token, req).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if (response.isSuccessful()) {
                            if(response.body().isSuccessful()) {
                                Navigation.findNavController(view).navigate(R.id.action_petGroupCreateFragment_to_drawerNav_petGroupListFragment);
                            }
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("1122", String.valueOf(t));
                    }
                });
            }
        });

        choose_pets = v.findViewById(R.id.petGroupCreate_choosePets);
        choose_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose pets");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = (View) inflater.inflate(R.layout.custom_alert_dialog, null);

                builder.setView(dialogView);

                RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.dialog_recycler);
                LinearLayoutManager man = new LinearLayoutManager(v.getContext());
                rv.setLayoutManager(man);
                rv.setHasFixedSize(true);
                PetCheckboxAdapter adapter = new PetCheckboxAdapter(pets, getActivity(), chosen_pets);
                rv.setAdapter(adapter);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosen_pets = adapter.getChecked();
                        petsChosenText.setText(chosen_pets.size() + " pet(s) chosen");
                        if(chosen_pets.size() > 0) {
                            petsChosenText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.rich_black,null));
                            petsChosenText.setTypeface(null, Typeface.ITALIC);
                        } else {
                            petsChosenText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.error_red, null));
                            petsChosenText.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_petGroupCreateFragment_to_drawerNav_petGroupListFragment);
            }
        });

        return v;

    }

    private Long getZoneId(String name) {
        for (Zone z : zones) {
            if (z.getName().equals(name)) {
                return z.getId();
            }
        }
        return -1L;
    }
    public boolean allFieldsValid() {
        boolean valid = true;
        if (name.getError() != null || name.getEditText().getText().toString().isEmpty()) {
            name.setError(getResources().getString(R.string.name_requirements));
            valid = false;
        }
        if (chosen_pets.size() == 0) {
            petsChosenText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.error_red, null));
            petsChosenText.setTypeface(null, Typeface.BOLD_ITALIC);
            valid = false;
        }
        return valid;
    }

}