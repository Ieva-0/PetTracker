package edu.ktu.pettrackerclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.retrofit.DeviceApi;
import edu.ktu.pettrackerclient.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceCreateEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_create_edit);

        initializeComponents();

    }

    public void initializeComponents() {
        EditText nameInput = findViewById(R.id.createDevice_name);
        EditText idInput = findViewById(R.id.createDevice_name);
        Button saveButton = findViewById(R.id.deviceCreate_btn);

        RetrofitService retrofitService = new RetrofitService();
        DeviceApi deviceApi = retrofitService.getRetrofit().create(DeviceApi.class);
        saveButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString();
            String id = idInput.getText().toString();

            Device e = new Device();
            e.setName(name);
            e.setId(id);
            e.setUser_id_foreign(1);

            deviceApi.add(e)
                    .enqueue(new Callback<Device>() {
                        @Override
                        public void onResponse(Call<Device> call, Response<Device> response) {
                            Toast.makeText(DeviceCreateEditActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Device> call, Throwable t) {
                            Toast.makeText(DeviceCreateEditActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(DeviceCreateEditActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
                        }
                    });
        });
    }
}