package edu.ktu.pettrackerclient.users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenticationApi {

    @POST("/api/auth/signin")
    Call<JwtResponse> login(@Body LoginRequest req);
    @POST("/api/auth/signup")
    Call<JwtResponse> register(@Body SignupRequest req);
    @POST("/api/auth/refreshtoken")
    Call<JwtResponse> refresh(@Body TokenRefreshRequest req);

}
