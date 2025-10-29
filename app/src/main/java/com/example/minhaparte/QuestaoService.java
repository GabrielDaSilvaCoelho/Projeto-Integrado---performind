package com.example.minhaparte;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestaoService {

    private static final String BASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Salvar questionário
    public static long criarQuestionario(long usuarioId, String titulo, String descricao) {
        try {
            JSONObject json = new JSONObject();
            json.put("usuario_id", usuarioId);
            json.put("titulo", titulo);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/rest/v1/questionarios")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray arr = new JSONArray(response.body().string());
                long id = arr.getJSONObject(0).getLong("id");
                Log.d("SUPABASE", "✅ Questionário criado: ID=" + id);
                return id;
            } else {
                Log.e("SUPABASE", "❌ Erro ao criar questionário: " + response.code());
                return -1;
            }
        } catch (Exception e) {
            Log.e("SUPABASE", "❌ Exceção: " + e.getMessage());
            return -1;
        }
    }

    // Salvar questão com ou sem questionário
    public static boolean salvarQuestaoComQuestionario(long questionarioId, long usuarioId, String enunciado, String[] alternativas, int indiceCorreta) {
        try {
            JSONObject json = new JSONObject();
            if (questionarioId > 0) json.put("questionario_id", questionarioId);
            json.put("usuario_id", usuarioId);
            json.put("enunciado", enunciado);
            json.put("alternativas", new JSONArray(alternativas));
            json.put("indice_correta", indiceCorreta);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/rest/v1/questoes")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("SUPABASE", "✅ Questão salva com sucesso!");
                return true;
            } else {
                Log.e("SUPABASE", "❌ Erro ao salvar questão: " + response.code() + " -> " + response.body().string());
                return false;
            }
        } catch (IOException e) {
            Log.e("SUPABASE", "❌ Erro de conexão: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("SUPABASE", "❌ Erro inesperado: " + e.getMessage());
            return false;
        }
    }
}
