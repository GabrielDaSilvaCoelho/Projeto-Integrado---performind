package com.example.minhaparte.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FlaskApiService {

    // Vai chamar: BASE_URL + "avaliar"
    // Ex.: http://10.42.30.56:5000/avaliar
    @POST("avaliar")
    Call<AvaliacaoResponse> avaliarDesempenho(@Body AvaliacaoRequest request);
}
