package com.example.minhaparte.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;



    public interface FlaskApiService {

        @POST("avaliar")
        Call<AvaliacaoResponse> avaliarDesempenho(@Body AvaliacaoRequest request);
    }

