package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.ZonePoint;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ZonePointApi {
    @POST("/zone_points/create")
    Call<List<ZonePoint>> addZonePoints(@Header("Authorization") String token, @Body List<ZonePoint> points);
    @GET("/zone_points/zone")
    Call<List<ZonePoint>> getZonePointsForZone(@Header("Authorization") String token, @Query("zone_id") Long zone_id);
}
