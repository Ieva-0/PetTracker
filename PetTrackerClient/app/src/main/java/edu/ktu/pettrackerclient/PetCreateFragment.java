package edu.ktu.pettrackerclient;

import static okhttp3.internal.Util.closeQuietly;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import java.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ktu.pettrackerclient.adapter.UploadResponse;
import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.FileUploadApi;
import edu.ktu.pettrackerclient.retrofit.PetApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import edu.ktu.pettrackerclient.retrofit.ZoneApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

public class PetCreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Spinner device_select;
    Button save_pet;
    EditText pet_name;
    List<Device> devices;
    Device selectedDevice;
    Spinner zone_select;
    List<Zone> zones;
    Zone selectedZone;
    Button cancel;
    ImageView compressedImg;
    Uri pickedImg;
    byte[] toSave;
    Button upload;
    Switch notif;
    boolean currentNotifsValue;

    Pet editting;
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
        String token =  pref.getString("tokenType", null) + " " + pref.getString("accessToken", null);
        Long user_id = pref.getLong("user_id", 0);

        View v =  inflater.inflate(R.layout.fragment_pet_create, container, false);

        device_select = v.findViewById(R.id.petCreate_devicePicker);
        device_select.setOnItemSelectedListener(this);
        zone_select = v.findViewById(R.id.petCreate_zonePicker);
        zone_select.setOnItemSelectedListener(this);
        save_pet = v.findViewById(R.id.petCreate_btn);
        cancel = v.findViewById(R.id.petCreate_cancel);
        pet_name = v.findViewById(R.id.petCreate_name);
        upload = v.findViewById(R.id.petCreate_upload);
        compressedImg = v.findViewById(R.id.petCreate_image2);
        notif = v.findViewById(R.id.petCreate_enableNotifications);
        currentNotifsValue = false;
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentNotifsValue = !currentNotifsValue;
            }
        });
//        device_name.setError("uwu");

        if(getArguments() != null) {
            Long pet_id = getArguments().getLong("pet_id");
            if(!pet_id.equals(0L)) {
                PetApi petapi = retrofitService.getRetrofit().create(PetApi.class);
                petapi.getPetById(token, pet_id).enqueue(new Callback<Pet>() {
                    @Override
                    public void onResponse(Call<Pet> call, Response<Pet> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            editting = response.body();
                            pet_name.setText(editting.getName().toString());
                            if(editting.getPicture()!= null) {
                                byte[] arr = Base64.getDecoder().decode(editting.getPicture());
                                toSave = arr;
                                Bitmap b2 = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                                if(b2 != null) {
                                    compressedImg.setImageBitmap(Bitmap.createScaledBitmap(b2, 120, 120, false));
                                }
                            }

                            notif.setChecked(editting.isNotifications());
                            currentNotifsValue = editting.isNotifications();

                        }
                    }

                    @Override
                    public void onFailure(Call<Pet> call, Throwable t) {

                    }
                });
            }
        } else {
            editting = new Pet();
        }

        deviceApi.getAvailableDevicesForUser(token, user_id)
                .enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        List<String> device_names = new ArrayList<>();
                        devices = new ArrayList<>();
                        String oldDevice = "";
                        Long oldDeviceId = 0L;
                        if(getArguments() != null) {
                            oldDeviceId = getArguments().getLong("fk_device_id");
                        }
                        device_names.add("");
                        for(Device o : response.body())
                        {
                            device_names.add(o.getName());
                            devices.add(o);
                            if(o.getId().equals(oldDeviceId)) {
                                oldDevice = o.getName();
                                selectedDevice = o;
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, device_names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        device_select.setAdapter(adapter);
                        device_select.setSelection(adapter.getPosition(oldDevice));

                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {

                    }
                });

        zoneApi.getAllZonesForUser(token, user_id)
                .enqueue(new Callback<List<Zone>>() {
                    @Override
                    public void onResponse(Call<List<Zone>> call, Response<List<Zone>> response) {
                        List<String> zone_names = new ArrayList<>();
                        zones = new ArrayList<>();
                        String oldZone = "None";
                        Long oldZoneId = 0L;
                        if(getArguments() != null) {
                            oldZoneId = getArguments().getLong("fk_zone_id");
                        }
                        zone_names.add("None");
                        for(Zone o : response.body())
                        {
                            zone_names.add(o.getName());
                            zones.add(o);
                            if(o.getId().equals(oldZoneId)) {
                                oldZone = o.getName();
                                selectedZone = o;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, zone_names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        zone_select.setAdapter(adapter);
                        zone_select.setSelection(adapter.getPosition(oldZone));

                    }

                    @Override
                    public void onFailure(Call<List<Zone>> call, Throwable t) {

                    }
                });
        save_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editting.setName(pet_name.getText().toString());
                editting.setFk_user_id(user_id);
                if (selectedDevice != null)
                    editting.setFk_device_id(selectedDevice.getId());
                if (selectedZone != null)
                    editting.setFk_zone_id(selectedZone.getId());
                if(toSave != null) {
                    editting.setPicture(Base64.getEncoder().encodeToString(toSave));
                }
                editting.setNotifications(currentNotifsValue);
                Log.d("1122", "saving pet object: " + editting);
                PetApi petApi = retrofitService.getRetrofit().create(PetApi.class);
                petApi.addPet(token, editting)
                        .enqueue(new Callback<Pet>() {
                            @Override
                            public void onResponse(Call<Pet> call, Response<Pet> response) {
                                Log.d("1122", "created " + response.body().toString());
                                Toast.makeText(getContext(), "successfully created pet", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_petCreateFragment_to_drawerNav_petList);
                            }

                            @Override
                            public void onFailure(Call<Pet> call, Throwable t) {
                                Toast.makeText(getContext(), "failed to create device", Toast.LENGTH_SHORT).show();
                            }
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
                        if(orientation == 6) {
                            m.postRotate(90);
                        }
                        //crop and scale image to 500x500
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                        int longer = 0;
                        int shorter = 0;
                        boolean widthLonger = true;
                        if(bitmap.getHeight() > bitmap.getWidth()) {
                            longer = bitmap.getHeight();
                            shorter = bitmap.getWidth();
                            widthLonger = false;
                        } else {
                            shorter = bitmap.getHeight();
                            longer = bitmap.getWidth();
                            widthLonger = true;
                        }

                        int diff = longer - shorter;
                        int offset = diff >= 2 ? diff/2 : 0;
                        int x = widthLonger ? offset : 0;
                        int y = widthLonger ? 0 : offset;
                        Bitmap bFinal = Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap, x, y, shorter, shorter, m, true), 500, 500, true);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.petCreate_devicePicker) {
            Log.d("1122", "device selected: " + adapterView.getItemAtPosition(i));
            if(adapterView.getItemAtPosition(i) != "") {
                devices.forEach(o -> {
                    if(o.getName() == adapterView.getItemAtPosition(i))
                        selectedDevice = o;
                });
            } else {
                selectedDevice = null;
            }
        }
        if(adapterView.getId() == R.id.petCreate_zonePicker) {
            Log.d("1122", "zone selected: " + adapterView.getItemAtPosition(i));
            if(adapterView.getItemAtPosition(i) != "") {
                zones.forEach(o -> {
                    if(o.getName() == adapterView.getItemAtPosition(i))
                        selectedZone = o;
                });
            } else {
                selectedZone = null;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}