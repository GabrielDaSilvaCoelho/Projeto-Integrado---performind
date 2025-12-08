package com.example.minhaparte.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


    private static final String BASE_URL = "http://127.0.0.1:5000/";

    private static FlaskApiService apiService;

    public static FlaskApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)              
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(FlaskApiService.class);
        }
        return apiService;
    }
}
