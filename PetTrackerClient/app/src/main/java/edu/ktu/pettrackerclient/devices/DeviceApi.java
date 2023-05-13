package edu.ktu.pettrackerclient.devices;

import android.os.Message;

import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.devices.Device;
import edu.ktu.pettrackerclient.devices.DeviceWithDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeviceApi {
    @GET("/devices/all_details")
    Call<List<DeviceWithDetails>> getAllDevicesWithDetailsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);
    @GET("/devices/device")
    Call<Device> getDeviceById(@Header("Authorization") String token, @Query("device_id") Long device_id);

    @POST("/devices/save")
    Call<MessageResponse> addDevice(@Header("Authorization") String token, @Body Device device);

    @DELETE("/devices/delete")
    Call<MessageResponse> deleteDevice(@Header("Authorization") String token, @Query("device_id") Long device_id);
}
