package edu.ktu.pettrackerclient.events;

import java.util.List;

import edu.ktu.pettrackerclient.events.Event;
import edu.ktu.pettrackerclient.events.EventWithDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventApi {
    @GET("/events/all_details")
    Call<List<EventWithDetails>> getEventsWithDetailsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

}
