package com.example.minhaparte.Service;

import com.example.minhaparte.Model.VideoModel;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
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
    Call<List<VideoModel>> getAllVideos(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );
    @DELETE("rest/v1/video")
    Call<ResponseBody> deleteVideoById(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("id") String idFilter
    );
    @DELETE("storage/v1/object/{bucket}/{file}")
    Call<ResponseBody> deleteStorageFile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Path("bucket") String bucket,
            @Path("file") String fileName
    );
}
