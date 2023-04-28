package edu.ktu.pettrackerclient.retrofit;

import edu.ktu.pettrackerclient.model.Device;
import edu.ktu.pettrackerclient.model.JwtResponse;
import edu.ktu.pettrackerclient.model.LoginRequest;
import edu.ktu.pettrackerclient.model.SignupRequest;
import edu.ktu.pettrackerclient.model.TokenRefreshRequest;
import edu.ktu.pettrackerclient.model.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenticationApi {

    @POST("/api/auth/signin")
    Call<JwtResponse> login(@Body LoginRequest req);
    @POST("/api/auth/signup")
    Call<JwtResponse> register(@Body SignupRequest req);
    @POST("/api/auth/refreshtoken")
    Call<JwtResponse> refresh(@Body TokenRefreshRequest req);

    @POST("/api/auth/changepassword")
    Call<Void> changePassword(@Header("Authorization") String token,  @Body User user);
}
