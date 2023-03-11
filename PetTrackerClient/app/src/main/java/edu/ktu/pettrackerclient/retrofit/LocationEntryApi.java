package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.LocationEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LocationEntryApi {
    @GET("/locations/last")
    Call<LocationEntry> getLastForDevice(@Header("Authorization") String token, @Query("device_id") Long device);

    @GET("/locations/history")
    Call<List<LocationEntry>> getHistoryForDevice(@Header("Authorization") String token, @Query("device_id") Long device);

}
