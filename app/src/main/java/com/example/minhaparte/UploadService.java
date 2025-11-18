package com.example.minhaparte;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UploadService {
    @Multipart
    @POST("storage/v1/object/video/{filename}")
    Call<ResponseBody> uploadVideo(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Path("filename") String filename,
            @Part MultipartBody.Part file
    );
    @Multipart
    @POST("storage/v1/object/thumb/{filename}")
    Call<ResponseBody> uploadThumb(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Path("filename") String filename,
            @Part MultipartBody.Part file
    );
    @POST("rest/v1/video")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> saveVideoData(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Body VideoModel model
    );
    @GET("rest/v1/video?select=*")
    Call<java.util.List<VideoModel>> getAllVideos(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );
}
