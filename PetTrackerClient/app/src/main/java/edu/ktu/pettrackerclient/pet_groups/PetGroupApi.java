package edu.ktu.pettrackerclient.pet_groups;

import java.util.List;

import edu.ktu.pettrackerclient.MessageResponse;
import edu.ktu.pettrackerclient.pets.Pet;
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

    @GET("/pet_groups/all_details")
    Call<List<PetGroupWithDetails>> getAllPetGroupsWithDetailsForUser(@Header("Authorization") String token, @Query("user_id") Long user_id);

    @GET("/pet_groups/group_create_edit")
    Call<PetGroupCreateEditResponse> getPetGroupById(@Header("Authorization") String token, @Query("pet_group_id") Long pet_group_id);
    @POST("/pet_groups/save")
    Call<MessageResponse> addPetGroup(@Header("Authorization") String token, @Body PetGroupCreateEditRequest pet_group);

    @DELETE("/pet_groups/delete")
    Call<MessageResponse> deletePetGroup(@Header("Authorization") String token, @Query("pet_group_id") Long pet_group_id);

}
