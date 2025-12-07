package com.example.minhaparte.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.minhaparte.Adapter.VideoAdapter;
import com.example.minhaparte.Model.VideoModel;
import com.example.minhaparte.R;
import com.example.minhaparte.Service.SupabaseApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

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
        window.setStatusBarColor(getResources().getColor(R.color.blue_500));

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnUser = findViewById(R.id.btnUser);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnTouchListener((v, event) -> onTouchEvent(event));

        // Só pra debug: ver o tipo salvo
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String tipo = prefs.getString("tipo_usuario", "N/A");
        Toast.makeText(this, "tipo_usuario no Feed: " + tipo, Toast.LENGTH_SHORT).show();

        // 🔥 FORÇA o botão do menu a aparecer SEM condições
        btnMenu.setVisibility(View.VISIBLE);
        btnMenu.setOnClickListener(v -> showMenuLateral());

        // Botão de usuário abre InfosDeUsuario
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, InfosDeUsuario.class);
            startActivity(intent);
        });

        // Swipe to refresh: recarrega vídeos
        swipeRefresh.setOnRefreshListener(this::loadVideos);

        // Carrega vídeos na entrada
        loadVideos();
    }

    private void loadVideos() {
        swipeRefresh.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApi api = retrofit.create(SupabaseApi.class);

        api.getVideos(API_KEY, AUTH).enqueue(new Callback<List<VideoModel>>() {
            @Override
            public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {
                swipeRefresh.setRefreshing(false);

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
                swipeRefresh.setRefreshing(false);
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

        // fundo escurecido
        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dimView.setBackgroundColor(Color.parseColor("#80000000"));
        drawerLayout.addView(dimView);

        // inflar layout do menu lateral
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

        // Botões do menu lateral
        Button btnTrocarSenha = menuView.findViewById(R.id.btnTrocarSenha);
        Button btnInfosUsuario = menuView.findViewById(R.id.btnInfosUsuario);
        Button btnEnquetes = menuView.findViewById(R.id.btnEnquetes);

        btnTrocarSenha.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, TrocarSenhaActivity.class);
            startActivity(intent);
        });

        btnInfosUsuario.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, InfosDeUsuario.class);
            startActivity(intent);
        });

        btnEnquetes.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(FeedActivity.this, EnquetesActivity.class);
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
    public boolean onTouchEvent(MotionEvent event) {
        // se não estiver usando swipe de tela pro lado, pode até remover isso
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX > 0) {
                        // swipe right
                    } else {
                        // swipe left
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVideos();
    }
}
