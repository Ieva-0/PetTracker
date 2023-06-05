package edu.ktu.pettrackerclient.pets;

import static okhttp3.internal.Util.closeQuietly;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;

import java.util.Base64;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.R;
import edu.ktu.pettrackerclient.devices.Device;
import edu.ktu.pettrackerclient.zones.Zone;
import edu.ktu.pettrackerclient.devices.DeviceApi;
import edu.ktu.pettrackerclient.RetrofitService;
import edu.ktu.pettrackerclient.zones.ZoneApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetCreateFragment extends Fragment {

    Button save_pet;
    TextInputLayout pet_name;
    List<Device> devices;
    List<Zone> zones;
    Button cancel;
    ImageView compressedImg;
    Uri pickedImg;
    byte[] toSave;
    Button upload;
    Switch notif;
    boolean currentNotifsValue;

    MaterialAutoCompleteTextView pick_zone;
    MaterialAutoCompleteTextView pick_device;
    PetWithDetails editting;

    public PetCreateFragment() {
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
        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        ZoneApi zoneApi = retrofitService.getRetrofit().create(ZoneApi.class);

        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token = pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        View v = inflater.inflate(R.layout.fragment_pet_create, container, false);


        save_pet = v.findViewById(R.id.petCreate_btn);
        cancel = v.findViewById(R.id.petCreate_cancel);
        pet_name = v.findViewById(R.id.petCreate_name);

        pet_name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pet_name.getEditText().getText().length() < 4 || pet_name.getEditText().getText().length() > 20)
                    pet_name.setError(getResources().getString(R.string.name_requirements));
                else pet_name.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        upload = v.findViewById(R.id.petCreate_upload);
        compressedImg = v.findViewById(R.id.petCreate_image2);
        notif = v.findViewById(R.id.petCreate_enableNotifications);
        currentNotifsValue = false;

        pick_zone = v.findViewById(R.id.pickZone_auto);
        pick_device = v.findViewById(R.id.pickDevice_auto);
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentNotifsValue = !currentNotifsValue;
            }
        });
        Long pet_id = 0L;
        if (getArguments() != null) {
            pet_id = getArguments().getLong("pet_id");
        } else {
            editting = new PetWithDetails();
        }

        PetApi petapi = retrofitService.getRetrofit().create(PetApi.class);
        petapi.getPetById(token, pet_id).enqueue(new Callback<PetCreateEditResponse>() {
            @Override
            public void onResponse(Call<PetCreateEditResponse> call, Response<PetCreateEditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //pet-------------------------
                    Log.d("1122", response.body().toString());
                    editting = response.body().getPet();
                    if (editting != null) {
                        pet_name.getEditText().setText(editting.getName().toString());

                        if (editting.getPicture() != null) {
                            byte[] arr = Base64.getDecoder().decode(editting.getPicture());
                            toSave = arr;
                            Bitmap b2 = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                            if (b2 != null) {
                                compressedImg.setImageBitmap(Bitmap.createScaledBitmap(b2, 120, 120, false));
                            }
                        }

                        notif.setChecked(editting.isNotifications());
                        currentNotifsValue = editting.isNotifications();
                    }

                    //devices-------------------------

                    List<String> device_names = new ArrayList<>();
                    devices = new ArrayList<>();
                    device_names.add("");
                    Device oldDeviceObj = null;
                    if (editting != null && editting.getDevice() != null) {
                        oldDeviceObj = editting.getDevice();
                        device_names.add(oldDeviceObj.getName());
                        devices.add(oldDeviceObj);
                    }
                    for (Device o : response.body().getDevices()) {
                        device_names.add(o.getName());
                        devices.add(o);
                    }
                    Log.d("1122", device_names.toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, device_names);
                    pick_device.setAdapter(adapter);
                    if (oldDeviceObj != null)
                        pick_device.setText(oldDeviceObj.getName(), false);

                    // zones-------------------------

                    List<String> zone_names = new ArrayList<>();
                    zones = new ArrayList<>();
                    zone_names.add("");
                    Zone oldZoneObj = null;
                    if (editting != null && editting.getZone() != null) {
                        oldZoneObj = editting.getZone();
                        zone_names.add(oldZoneObj.getName());
                        zones.add(oldZoneObj);
                    }
                    for (Zone o : response.body().getZones()) {
                        zone_names.add(o.getName());
                        zones.add(o);
                    }
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, zone_names);
                    pick_zone.setAdapter(adapter2);
                    if (oldZoneObj != null)
                        pick_zone.setText(oldZoneObj.getName(), false);
                }
            }

            @Override
            public void onFailure(Call<PetCreateEditResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.d("1122", String.valueOf(t));
            }
        });
        save_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pet_name.getError() != null || pet_name.getEditText().getText().toString().isEmpty()) {
                    pet_name.setError(getResources().getString(R.string.name_requirements));
                    Toast.makeText(getContext(), "Please check your inputs.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pet p = new Pet();
                if(editting != null && editting.getId() != null)
                    p.setId(editting.getId());
                p.setName(pet_name.getEditText().getText().toString());
                p.setFk_user_id(user_id);
                if (getDeviceId(pick_device.getText().toString()) > 0)
                    p.setFk_device_id(getDeviceId(pick_device.getText().toString()));
                if (getZoneId(pick_zone.getText().toString()) > 0)
                    p.setFk_zone_id(getZoneId(pick_zone.getText().toString()));
                if (toSave != null) {
                    p.setPicture(Base64.getEncoder().encodeToString(toSave));
                }
                if(editting != null)
                    editting.setNotifications(currentNotifsValue);
                PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
                petApi.savePet(token, p)
                        .enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                if (response.isSuccessful()) {
                                    if(response.body().isSuccessful()) {
                                        Navigation.findNavController(view).navigate(R.id.action_petCreateFragment_to_drawerNav_petList);
                                    }
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<MessageResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                Log.d("1122", String.valueOf(t));                            }
                        });


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_petCreateFragment_to_drawerNav_petList);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(i);
            }
        });
        return v;
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    Uri photoUri = result.getData().getData();
                    pickedImg = photoUri;
                    try {
                        //to make sure photo is rotated correctly
                        InputStream inputStream = getContext().getContentResolver().openInputStream(photoUri);
                        ExifInterface exif = new ExifInterface(inputStream);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Matrix m = new Matrix();
                        if (orientation == 6) {
                            m.postRotate(90);
                        }
                        //crop and scale image to 500x500
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                        int longer = 0;
                        int shorter = 0;
                        boolean widthLonger = true;
                        if (bitmap.getHeight() > bitmap.getWidth()) {
                            longer = bitmap.getHeight();
                            shorter = bitmap.getWidth();
                            widthLonger = false;
                        } else {
                            shorter = bitmap.getHeight();
                            longer = bitmap.getWidth();
                            widthLonger = true;
                        }

                        int diff = longer - shorter;
                        int offset = diff >= 2 ? diff / 2 : 0;
                        int x = widthLonger ? offset : 0;
                        int y = widthLonger ? 0 : offset;
                        Bitmap bFinal = Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap, x, y, shorter, shorter, m, true), 80, 80, true);
                        compressedImg.setImageBitmap(bFinal);
                        // convert image to bytes to save in db
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bFinal.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bArray = bos.toByteArray();
                        toSave = bArray;
                        Log.d("1122", String.valueOf(bArray));
                        Log.d("1122", String.valueOf(bArray.length));

                    } catch (IOException e) {
                        Log.d("1122", String.valueOf(e));
                    }
                }
            }
    );

    private Long getZoneId(String name) {
        for (Zone z : zones) {
            if (z.getName().equals(name)) {
                return z.getId();
            }
        }
        return -1L;
    }

    private Long getDeviceId(String name) {
        for (Device d : devices) {
            if (d.getName().equals(name)) {
                return d.getId();
            }
        }
        return -1L;
    }


}