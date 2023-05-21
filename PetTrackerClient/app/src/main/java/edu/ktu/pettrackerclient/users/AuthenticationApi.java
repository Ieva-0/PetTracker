package edu.ktu.pettrackerclient.users;

import edu.ktu.pettrackerclient.MessageResponse;
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

    @POST("/api/auth/firebase_token")
    Call<MessageResponse> firebase_token(@Body User user);

}
