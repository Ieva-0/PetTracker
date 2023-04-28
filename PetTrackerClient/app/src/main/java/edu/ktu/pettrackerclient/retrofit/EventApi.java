package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Event;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.PetGroupCreateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventApi {
    @GET("/events/all")
    Call<List<Event>> getEventsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/events/pet")
    Call<List<Event>> getEventsForPet(@Header("Authorization") String token, @Query("pet_id") Long pet_id);

}
