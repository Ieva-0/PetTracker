package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.Pet;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PetApi {
    @GET("/pets/all")
    Call<List<Pet>> getAllPetsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/pets/device")
    Call<Pet> getPetById(@Header("Authorization") String token, @Query("pet_id") Long pet_id);

    @POST("/pets/create")
    Call<Pet> addPet(@Header("Authorization") String token, @Body Pet pet);

    @DELETE("/pets/delete")
    Call<Void> deletePet(@Header("Authorization") String token, @Query("pet_id") Long pet_id);
}
