package edu.ktu.pettrackerclient.zones;

import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.zones.Zone;
import edu.ktu.pettrackerclient.zones.ZoneWithPoints;
import edu.ktu.pettrackerclient.zones.ZoneWithDetails;
import edu.ktu.pettrackerclient.zones.ZonesForDeviceResponse;
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

    @GET("/zones/all_details")
    Call<List<ZoneWithDetails>> getAllZonesWithDetailsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/zones/all_with_points")
    Call<ZonesForDeviceResponse> getAllZonesWithDetailsForDevice(@Header("Authorization") String token, @Query("device_id") Long device_id);
    @GET("/zones/zone_with_points")
    Call<ZoneWithPoints> getZoneWithPoints(@Header("Authorization") String token, @Query("zone_id") Long zone_id);

    @POST("/zones/create")
    Call<MessageResponse> addZone(@Header("Authorization") String token, @Body ZoneWithPoints zone);

    @DELETE("/zones/delete")
    Call<MessageResponse> deleteZone(@Header("Authorization") String token, @Query("zone_id") Long zone_id);
}
