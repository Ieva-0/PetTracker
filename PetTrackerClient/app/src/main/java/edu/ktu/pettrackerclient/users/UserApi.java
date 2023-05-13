package edu.ktu.pettrackerclient.users;

import edu.ktu.pettrackerclient.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {
    @GET("/users/all_with_roles")
    Call<UsersWithRolesResponse> getAllUsersWithRoles(@Header("Authorization") String token);

    @GET("/users/user")
    Call<User> getUser(@Header("Authorization") String token);

    @POST("/users/toggle_block")
    Call<MessageResponse> toggleBlockUser(@Header("Authorization") String token,  @Query("user_id") Long user_id);

    @DELETE("/users/user")
    Call<MessageResponse> deleteUser(@Header("Authorization") String token, @Query("user_id") Long user_id);


    @POST("/users/change_password")
    Call<MessageResponse> changePassword(@Header("Authorization") String token,  @Body ChangePasswordRequest req);

    @POST("/users/edit_profile")
    Call<MessageResponse> editProfile(@Header("Authorization") String token,  @Body User user);

}
