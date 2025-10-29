package com.example.minhaparte;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoService {

    private static final String BASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String API_KEY = "SUA_SUPABASE_API_KEY";
    private static final OkHttpClient client = new OkHttpClient();

    public static void uploadVideo(Context context, Uri videoUri, String titulo, String descricao, long usuarioId) {
        new Thread(() -> {
            try {
                // Ler bytes do arquivo
                InputStream is = context.getContentResolver().openInputStream(videoUri);
                byte[] videoBytes = new byte[is.available()];
                is.read(videoBytes);
                is.close();

                // Nome do arquivo
                String fileName = "video_" + System.currentTimeMillis() + ".mp4";

                // 🔹 Upload para Supabase Storage
                RequestBody fileBody = RequestBody.create(videoBytes, MediaType.parse("video/mp4"));
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName, fileBody)
                        .build();

                Request uploadRequest = new Request.Builder()
                        .url(BASE_URL + "/storage/v1/object/videos/" + fileName)
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .put(requestBody)
                        .build();

                Response uploadResponse = client.newCall(uploadRequest).execute();
                if (!uploadResponse.isSuccessful()) {
                    Log.e("VideoService", "Erro ao enviar para storage: " + uploadResponse.code());
                    showToast(context, "Erro ao enviar vídeo: " + uploadResponse.code());
                    return;
                }

                // 🔹 URL pública do vídeo
                String publicUrl = BASE_URL + "/storage/v1/object/public/videos/" + fileName;

                // 🔹 Salvar no banco Supabase
                JSONObject json = new JSONObject();
                json.put("usuario_id", usuarioId);
                json.put("titulo", titulo);
                json.put("descricao", descricao);
                json.put("url", publicUrl);

                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                Request dbRequest = new Request.Builder()
                        .url(BASE_URL + "/rest/v1/videos")
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .post(body)
                        .build();

                Response dbResponse = client.newCall(dbRequest).execute();
                if (dbResponse.isSuccessful()) {
                    VideoItem video = new VideoItem(titulo, publicUrl);
                    if (context instanceof UploadVideoActivity) {
                        ((UploadVideoActivity) context).runOnUiThread(() -> {
                            ((UploadVideoActivity) context).addVideoToFeed(video);
                            Toast.makeText(context, "✅ Vídeo enviado e salvo com sucesso!", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e("VideoService", "Erro ao salvar no banco: " + dbResponse.code());
                    showToast(context, "Erro ao salvar no banco: " + dbResponse.code());
                }

            } catch (Exception e) {
                Log.e("VideoService", "Erro: " + e.getMessage());
                showToast(context, "Erro ao enviar vídeo: " + e.getMessage());
            }
        }).start();
    }

    private static void showToast(Context context, String msg) {
        if (context instanceof UploadVideoActivity) {
            ((UploadVideoActivity) context).runOnUiThread(() ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
        }
    }
}
