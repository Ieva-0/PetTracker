package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.ZoneCreateFragment;
import edu.ktu.pettrackerclient.model.Zone;
import edu.ktu.pettrackerclient.model.ZoneCreateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ZoneApi {
    @GET("/zones/all")
    Call<List<Zone>> getAllZonesForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/zones/zone")
    Call<Zone> getZoneById(@Header("Authorization") String token, @Query("zone_id") Long zone_id);

    @POST("/zones/create")
    Call<Zone> addZone(@Header("Authorization") String token, @Body ZoneCreateRequest zone);

    @DELETE("/zones/delete")
    Call<Void> deleteZone(@Header("Authorization") String token, @Query("zone_id") Long zone_id);
}
