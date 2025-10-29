package com.example.minhaparte;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBHelper {

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    private final Context context;

    public DBHelper(Context context) {
        this.context = context;
    }

    // 🔹 LOGIN
    public interface LoginCallback {
        void onResult(boolean sucesso, int usuarioId);
    }

    public void login(String email, String senha, LoginCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?email=eq." + email + "&senha=eq." + senha);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();

                    JSONArray jsonArray = new JSONArray(sb.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject user = jsonArray.getJSONObject(0);
                        int id = user.getInt("id");
                        callback.onResult(true, id);
                    } else {
                        callback.onResult(false, -1);
                    }
                } else {
                    callback.onResult(false, -1);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SUPABASE", "Erro no login: " + e.getMessage());
                callback.onResult(false, -1);
            }
        }).start();
    }

    // 🔹 INSERIR QUESTÃO
    public void inserirQuestao(Question q, int usuarioId) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/questoes");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("usuario_id", usuarioId);
                json.put("enunciado", q.getQuestionText());
                json.put("alternativas", new JSONArray(q.getAlternatives()));
                json.put("indice_correta", q.getCorrectIndex());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                if (code == 201 || code == 200) {
                    Log.d("SUPABASE", "✅ Questão salva com sucesso!");
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    Log.e("SUPABASE", "❌ Erro ao inserir questão: " + sb);
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("SUPABASE", "Erro ao salvar questão: " + e.getMessage());
            }
        }).start();
    }
}
