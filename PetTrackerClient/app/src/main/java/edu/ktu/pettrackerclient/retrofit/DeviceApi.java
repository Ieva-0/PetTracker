package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Device;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DeviceApi {
    @GET("/device/all")
    Call<List<Device>> getAll();

    @POST("/device/create")
    Call<Device> add(@Body Device employee);
}
