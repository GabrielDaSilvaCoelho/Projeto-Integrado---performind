package com.example.minhaparte.All;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoModel> videos = new ArrayList<>();

    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
    private final String AUTH = "Bearer " + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        recyclerView = findViewById(R.id.recyclerVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VideoAdapter(videos, this, this::confirmarExclusao);
        recyclerView.setAdapter(adapter);

        carregarVideos();
    }

    private void carregarVideos() {
        SupabaseApi api = RetrofitClient.getClient().create(SupabaseApi.class);

        api.getVideos(API_KEY, AUTH).enqueue(new Callback<List<VideoModel>>() {
            @Override
            public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(VideoListActivity.this, "Erro ao carregar vídeos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                videos.clear();
                videos.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                Toast.makeText(VideoListActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarExclusao(VideoModel video) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir vídeo")
                .setMessage("Deseja realmente excluir \"" + video.title + "\"?")
                .setPositiveButton("Sim", (d, w) -> excluirVideo(video))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirVideo(VideoModel video) {

        UploadService api = RetrofitClient.getClient().create(UploadService.class);

        String videoFile = video.video_url.substring(video.video_url.lastIndexOf("/") + 1);
        String thumbFile = video.thumb_url.substring(video.thumb_url.lastIndexOf("/") + 1);

        // 1️⃣ excluir do banco
        api.deleteVideoById(API_KEY, AUTH, "eq." + video.id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (!response.isSuccessful()) {
                            Toast.makeText(VideoListActivity.this, "Erro ao excluir no banco!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 2️⃣ excluir vídeo do storage
                        api.deleteStorageFile(API_KEY, AUTH, "video", videoFile)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        // 3️⃣ excluir thumbnail do storage
                                        api.deleteStorageFile(API_KEY, AUTH, "thumb", thumbFile)
                                                .enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                                        Toast.makeText(VideoListActivity.this, "Vídeo excluído!", Toast.LENGTH_SHORT).show();
                                                        carregarVideos();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Toast.makeText(VideoListActivity.this, "Erro ao excluir thumbnail!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(VideoListActivity.this, "Erro ao excluir vídeo!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(VideoListActivity.this, "Erro ao excluir no banco!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

