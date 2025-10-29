package com.example.minhaparte;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO = 101;

    private EditText etTitulo;
    private Button btnSelecionar, btnUpload;
    private RecyclerView rvVideos;
    private Uri selectedVideoUri;

    private ArrayList<VideoItem> videoList = new ArrayList<>();
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        // Inicializar views
        etTitulo = findViewById(R.id.etTitulo);
        btnSelecionar = findViewById(R.id.btnSelecionar);
        btnUpload = findViewById(R.id.btnUpload);
        rvVideos = findViewById(R.id.rvVideos);

        // Configurar RecyclerView
        adapter = new VideoAdapter(this, videoList);
        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(adapter);

        // Selecionar vídeo
        btnSelecionar.setOnClickListener(v -> selectVideo());

        // Enviar vídeo
        btnUpload.setOnClickListener(v -> {
            if (selectedVideoUri == null) {
                Toast.makeText(this, "Selecione um vídeo primeiro!", Toast.LENGTH_SHORT).show();
                return;
            }

            String titulo = etTitulo.getText().toString().trim();
            if (titulo.isEmpty()) {
                Toast.makeText(this, "Digite o título do vídeo!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Usuário fixo 1 como exemplo
            VideoService.uploadVideo(this, selectedVideoUri, titulo, "", 1);

            // Limpar seleção e título
            selectedVideoUri = null;
            etTitulo.setText("");
        });

        // Carregar vídeos do banco (opcional)
        loadVideosFromDatabase();
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/mp4");
        startActivityForResult(Intent.createChooser(intent, "Selecionar vídeo"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            Toast.makeText(this, "Vídeo selecionado!", Toast.LENGTH_SHORT).show();
        }
    }

    // Adiciona vídeo ao feed
    public void addVideoToFeed(VideoItem video) {
        runOnUiThread(() -> {
            videoList.add(0, video);
            adapter.notifyItemInserted(0);
            rvVideos.scrollToPosition(0);
        });
    }

    // Aqui você pode implementar uma chamada para listar vídeos existentes no Supabase
    private void loadVideosFromDatabase() {
        // Exemplo: chamar endpoint REST para buscar vídeos
        // E então chamar addVideoToFeed(video) para cada vídeo retornado
    }
}
