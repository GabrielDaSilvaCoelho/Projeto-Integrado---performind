package com.example.minhaparte;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://pbpkxbkwfpznkkuwcxjl.supabase.co/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

//package com.example.minhaparte;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//
//public class RetrofitClient {
//    private static final String BASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
//    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
//
//    private static Retrofit retrofit;
//
//    public static Retrofit getClient() {
//        if (retrofit == null) {
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(chain -> {
//                        Request request = chain.request().newBuilder()
//                                .addHeader("apikey", API_KEY)
//                                .addHeader("Authorization", "Bearer " + API_KEY)
//                                .addHeader("Content-Type", "application/json")
//                                .build();
//                        return chain.proceed(request);
//                    })
//                    .build();
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
//                    .build();
//        }
//        return retrofit;
//    }
//}


