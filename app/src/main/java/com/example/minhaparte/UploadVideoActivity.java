package com.example.minhaparte;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private Button btnPickVideo, btnUploadVideo;
    private VideoView videoView;
    private EditText edtFileName;
    private Uri selectedVideoUri;

    // URL do seu bucket
    private final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co/storage/v1/object/videos/";
    private final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        videoView = findViewById(R.id.videoView);
        edtFileName = findViewById(R.id.edtFileName);

        // Pedir permissão
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 100);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }

        btnPickVideo.setOnClickListener(v -> pickVideo());
        btnUploadVideo.setOnClickListener(v -> {
            if (selectedVideoUri != null) uploadVideoToSupabase(selectedVideoUri);
            else Toast.makeText(this, "Escolha um vídeo primeiro", Toast.LENGTH_SHORT).show();
        });
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            videoView.setVideoURI(selectedVideoUri);
            videoView.start();
        }
    }

    private void uploadVideoToSupabase(Uri videoUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[8192];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] videoBytes = buffer.toByteArray();
            inputStream.close();

            String fileName = edtFileName.getText().toString().trim();
            if (fileName.isEmpty()) fileName = "video.mp4";
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(videoBytes, MediaType.parse("video/mp4"));
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + fileName)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(UploadVideoActivity.this, "Falha ao enviar: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(UploadVideoActivity.this, "Upload concluído!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UploadVideoActivity.this, "Erro no upload: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao ler o vídeo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
