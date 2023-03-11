package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Device;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeviceApi {
    @GET("/devices/all")
    Call<List<Device>> getAllDevicesForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/devices/device")
    Call<Device> getDeviceById(@Header("Authorization") String token, @Query("device_id") Long device_id);

    @POST("/devices/create")
    Call<Device> addDevice(@Header("Authorization") String token, @Body Device device);

    @DELETE("/devices/delete")
    Call<Void> deleteDevice(@Header("Authorization") String token, @Query("device_id") Long device_id);
}
