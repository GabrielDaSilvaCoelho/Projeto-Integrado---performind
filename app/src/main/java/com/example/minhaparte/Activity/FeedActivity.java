package com.example.minhaparte.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.minhaparte.Model.VideoModel;
import com.example.minhaparte.R;
import com.example.minhaparte.Service.SupabaseApi;
import com.example.minhaparte.Adapter.VideoAdapter;
import java.util.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
    private static final String AUTH = "Bearer " + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnTouchListener((av, event) -> onTouchEvent(event));

        loadVideos();
    }
    private void loadVideos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApi api = retrofit.create(SupabaseApi.class);

        api.getVideos(API_KEY, AUTH).enqueue(new Callback<List<VideoModel>>() {
            @Override
            public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new VideoAdapter(response.body(), FeedActivity.this, video -> {
                        Intent intent = new Intent(FeedActivity.this, PlayerActivity.class);
                        intent.putExtra("videoUrl", video.video_url);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadVideos();
    }
}
