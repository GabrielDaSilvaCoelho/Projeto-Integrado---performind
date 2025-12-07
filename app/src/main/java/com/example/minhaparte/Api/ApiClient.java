package com.example.minhaparte.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 🔥 PC rodando Flask na rede local
    private static final String BASE_URL = "http://192.168.1.9:5000/";
    private static FlaskApiService apiService;

    public static FlaskApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // PRECISA terminar com /
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(FlaskApiService.class);
        }
        return apiService;
    }
}
