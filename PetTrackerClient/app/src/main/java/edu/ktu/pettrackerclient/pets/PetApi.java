package edu.ktu.pettrackerclient.pets;

import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.pets.Pet;
import edu.ktu.pettrackerclient.pets.PetCreateEditResponse;
import edu.ktu.pettrackerclient.pets.PetWithDetails;
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
    @GET("/pets/all_details")
    Call<List<PetWithDetails>> getAllPetsForUserWithDetails(@Header("Authorization") String token, @Query("user_id") Long user_id);
    @GET("/pets/pet_edit_create")
    Call<PetCreateEditResponse> getPetById(@Header("Authorization") String token, @Query("pet_id") Long pet_id);

    @POST("/pets/save")
    Call<MessageResponse> savePet(@Header("Authorization") String token, @Body Pet pet);

    @DELETE("/pets/delete")
    Call<MessageResponse> deletePet(@Header("Authorization") String token, @Query("pet_id") Long pet_id);
}
