package com.example.minhaparte.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.Model.VideoModel;
import com.example.minhaparte.R;
import com.example.minhaparte.RetrofitClient;
import com.example.minhaparte.Service.UploadService;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {
    private static final int PICK_VIDEO = 100;
    private static final int PICK_IMAGE = 200;

    private Uri videoUri, thumbUri;
    private EditText title, description;
    private Button uploadBtn, pickVideoBtn, pickThumbBtn;
    private VideoView videoPreview;
    private ImageView thumbPreview;

    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM"; // coloque sua anon key
    private final String AUTH = "Bearer " + API_KEY;
    private final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));
        title = findViewById(R.id.editTitle);
        description = findViewById(R.id.editDescription);
        uploadBtn = findViewById(R.id.btnUpload);
        pickVideoBtn = findViewById(R.id.btnPickVideo);
        pickThumbBtn = findViewById(R.id.btnPickThumb);
        videoPreview = findViewById(R.id.videoPreview);
        thumbPreview = findViewById(R.id.thumbPreview);
        pickVideoBtn.setOnClickListener(v -> pickVideo());
        pickThumbBtn.setOnClickListener(v -> pickThumb());
        uploadBtn.setOnClickListener(v -> uploadFiles());
    }
    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);
    }
    private void pickThumb() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
    private void uploadFiles() {
        if (videoUri == null || thumbUri == null) {
            Toast.makeText(this, "Escolha o vídeo e a thumbnail!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Enviando...");
        dialog.setCancelable(false);
        dialog.show();

        UploadService service = RetrofitClient.getClient().create(UploadService.class);

        try {
            String videoName = System.currentTimeMillis() + ".mp4";
            String thumbName = System.currentTimeMillis() + ".jpg";

            File videoFile = createTempFile(videoUri, videoName);
            File imageFile = createTempFile(thumbUri, thumbName);

            MultipartBody.Part videoPart = MultipartBody.Part.createFormData(
                    "file", videoFile.getName(),
                    RequestBody.create(MediaType.parse("video/mp4"), videoFile));

            MultipartBody.Part thumbPart = MultipartBody.Part.createFormData(
                    "file", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), imageFile));

            service.uploadVideo(API_KEY, AUTH, videoName, videoPart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Erro ao enviar vídeo", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    service.uploadThumb(API_KEY, AUTH, thumbName, thumbPart).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            if (!response.isSuccessful()) {
                                Toast.makeText(UploadActivity.this, "Erro ao enviar thumbnail", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String videoUrl = SUPABASE_URL + "/storage/v1/object/public/video/" + videoName;
                            String thumbUrl = SUPABASE_URL + "/storage/v1/object/public/thumb/" + thumbName;
                            String videoTitle = title.getText().toString();
                            String videoDesc = description.getText().toString();
                            if (videoDesc.isEmpty()) videoDesc = "Sem descrição";

                            VideoModel model = new VideoModel(0,videoTitle, videoDesc, videoUrl, thumbUrl);

                            service.saveVideoData(API_KEY, AUTH, model).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(UploadActivity.this, "Upload completo!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(UploadActivity.this, "Erro ao salvar no banco: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(UploadActivity.this, "Erro ao enviar thumbnail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Erro ao enviar vídeo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private File createTempFile(Uri uri, String name) throws Exception {
        File file = new File(getCacheDir(), name);
        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
        }
        return file;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_VIDEO) {
                videoUri = data.getData();
                videoPreview.setVideoURI(videoUri);
                videoPreview.start();
            } else if (requestCode == PICK_IMAGE) {
                thumbUri = data.getData();
                thumbPreview.setImageURI(thumbUri);
            }
        }
    }
}
