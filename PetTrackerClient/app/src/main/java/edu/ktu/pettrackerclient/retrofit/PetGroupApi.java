package edu.ktu.pettrackerclient.retrofit;

import java.util.List;

import edu.ktu.pettrackerclient.model.Pet;
import edu.ktu.pettrackerclient.model.PetGroup;
import edu.ktu.pettrackerclient.model.PetGroupCreateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PetGroupApi {
    @GET("/pet_groups/all")
    Call<List<PetGroup>> getAllPetGroupsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/pet_groups/group")
    Call<PetGroup> getPetGroupById(@Header("Authorization") String token, @Query("pet_group_id") Long pet_group_id);

    @GET("/pet_groups/pet")
    Call<List<PetGroup>> getGroupsForPet(@Header("Authorization") String token, @Query("pet_id") Long pet_id);

    @GET("/pet_groups/pets")
    Call<List<Pet>> getPetsForGroup(@Header("Authorization") String token, @Query("group_id") Long group_id);

    @POST("/pet_groups/create")
    Call<PetGroup> addPetGroup(@Header("Authorization") String token, @Body PetGroupCreateRequest pet_group);

    @DELETE("/pet_groups/delete")
    Call<Void> deletePetGroup(@Header("Authorization") String token, @Query("pet_group_id") Long pet_group_id);

    @GET("/pet_groups/amount")
    Call<Integer> getPetAmountInGroup(@Header("Authorization") String token, @Query("pet_group_id") Long pet_group_id);
}
