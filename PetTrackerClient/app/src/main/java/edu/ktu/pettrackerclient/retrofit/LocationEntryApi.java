package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.LocationEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationEntryApi {
    @GET("/location/last")
    Call<LocationEntry> getLastForDevice(@Query("device_id_foreign") String device);

    @GET("/location/history")
    Call<List<LocationEntry>> getHistoryForDevice(@Query("device_id_foreign") String device);

    @GET("/location/last")
    Call<LocationEntry> getLast();

    @GET("/location/history")
    Call<List<LocationEntry>> getHistory();


}
