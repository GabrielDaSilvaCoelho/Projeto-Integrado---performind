package com.example.minhaparte.Activity;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestaoService {
    private static final String BASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static long criarQuestionario(long usuarioId, String titulo) {
        try {
            JSONObject json = new JSONObject();
            json.put("usuario_id", usuarioId);
            json.put("titulo", titulo);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/rest/v1/questionarios")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Prefer", "return=representation")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            String resposta = response.body().string();
            Log.d("SUPABASE", "Resposta criarQuestionario: " + resposta);

            if (!response.isSuccessful()) {
                Log.e("SUPABASE", "Erro criarQuestionario: " + response.code());
                return -1;
            }

            JSONArray arr = new JSONArray(resposta);
            long id = arr.getJSONObject(0).getLong("id");
            Log.d("SUPABASE", "Questionário criado com ID: " + id);
            return id;

        } catch (Exception e) {
            Log.e("SUPABASE", "Exceção criarQuestionario: " + e.getMessage(), e);
            return -1;
        }
    }
    public static boolean salvarQuestao(long questionarioId, long usuarioId,
                                        String pergunta, String[] alternativas, int correta) {
        try {
            JSONObject json = new JSONObject();
            json.put("questionario_id", questionarioId);
            json.put("usuario_id", usuarioId);
            json.put("pergunta", pergunta);
            json.put("alternativas", new JSONArray(alternativas));
            json.put("correta", correta);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/rest/v1/questoes")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            String resposta = response.body().string();
            Log.d("SUPABASE", "Resposta salvarQuestao: " + resposta);

            if (!response.isSuccessful()) {
                Log.e("SUPABASE", "Erro salvarQuestao: " + response.code());
                return false;
            }

            Log.d("SUPABASE", "Questão salva com sucesso");
            return true;

        } catch (Exception e) {
            Log.e("SUPABASE", "Exceção salvarQuestao: " + e.getMessage(), e);
            return false;
        }
    }
}
