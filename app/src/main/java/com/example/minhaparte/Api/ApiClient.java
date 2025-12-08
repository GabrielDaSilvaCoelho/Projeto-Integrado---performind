package com.example.minhaparte.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // EMULADOR Android Studio -> usa 10.0.2.2 para enxergar o PC
    private static final String BASE_URL = "http://10.0.2.2:5000/"; // <-- barra no final!

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
