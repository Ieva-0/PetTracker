package edu.ktu.pettrackerclient.retrofit;

import java.io.InputStream;

import edu.ktu.pettrackerclient.adapter.UploadResponse;
import edu.ktu.pettrackerclient.model.Pet;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FileUploadApi {

    @Multipart
    @POST("/upload")
    Call<UploadResponse> addFile(@Header("Authorization") String token, @Part MultipartBody.Part photo);

    @POST("/array")
    Call<UploadResponse> addFileArray(@Header("Authorization") String token, @Body byte[] photo);
    @GET("/array")
    Call<byte[]> getFileArray(@Header("Authorization") String token, @Query("filename") String filename);

    @POST("/stream")
    Call<UploadResponse> addFileStream(@Header("Authorization") String token, @Body InputStream photo);
}
