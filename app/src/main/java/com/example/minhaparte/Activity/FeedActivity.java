package com.example.minhaparte.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.Adapter.VideoAdapter;
import com.example.minhaparte.Model.VideoModel;
import com.example.minhaparte.R;
import com.example.minhaparte.Service.SupabaseApi;

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        ImageButton btnMenu = findViewById(R.id.btnMenu);

        // Pega o tipo de usuário
        String tipo = getSharedPreferences("user_data", MODE_PRIVATE)
                .getString("tipo", "Colaborador");

        // Somente usuários do tipo "RH" ou "Supervisor" veem o menu
        if ("RH".equalsIgnoreCase(tipo) || "Supervisor".equalsIgnoreCase(tipo)) {
            btnMenu.setVisibility(View.VISIBLE);
        } else {
            btnMenu.setVisibility(View.GONE);
        }

        btnMenu.setOnClickListener(v -> showMenuLateral());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnTouchListener((v, event) -> onTouchEvent(event));

        ImageButton btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, InfosDeUsuario.class);
            startActivity(intent);
        });

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

    @SuppressLint("WrongConstant")
    private void showMenuLateral() {
        DrawerLayout drawerLayout = new DrawerLayout(this);
        drawerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        drawerLayout.setFitsSystemWindows(true);

        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dimView.setBackgroundColor(Color.parseColor("#80000000"));
        drawerLayout.addView(dimView);

        View menuView = getLayoutInflater().inflate(R.layout.layout_menu_lateral, drawerLayout, false);
        int menuWidth = (int) (getScreenWidth() * 0.75);
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(
                menuWidth,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.START);
        menuView.setLayoutParams(params);
        drawerLayout.addView(menuView);

        dimView.setOnClickListener(v -> drawerLayout.closeDrawer(Gravity.START));

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                dimView.setAlpha(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
                rootView.removeView(drawerLayout);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        Button btnCriarUsuario = menuView.findViewById(R.id.btnCriarUsuario);
        Button btnCriarEnquete = menuView.findViewById(R.id.btnCriarEnquete);
        Button btnExcluirUsuario = menuView.findViewById(R.id.btnExcluirUsuario);
        Button btnUploadVideo = menuView.findViewById(R.id.btnUploadVideo);
        Button btnExcluirVideo = menuView.findViewById(R.id.btnExcluirVideo);


        btnCriarUsuario.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, CriarUsuarioActivity.class);
            startActivity(intent);
        });

        btnCriarEnquete.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        btnExcluirUsuario.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, ListarUsuariosActivity.class);
            startActivity(intent);
        });

        btnUploadVideo.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        btnExcluirVideo.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, VideoListActivity.class);
            startActivity(intent);
        });

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(drawerLayout);
        drawerLayout.openDrawer(Gravity.START);
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVideos();
    }
}
