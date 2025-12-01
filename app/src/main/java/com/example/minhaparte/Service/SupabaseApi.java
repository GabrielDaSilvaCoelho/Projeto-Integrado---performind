package com.example.minhaparte.Service;

import com.example.minhaparte.Model.VideoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SupabaseApi {
    @GET("rest/v1/video?select=*")
    Call<List<VideoModel>> getVideos(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );
}
