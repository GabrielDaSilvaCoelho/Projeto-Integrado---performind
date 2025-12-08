package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class EnquetesActivity extends AppCompatActivity {

    private ListView listEnquetes;
    private ArrayList<Long> enqueteIds = new ArrayList<>();
    private ArrayList<String> enqueteTitulos = new ArrayList<>();

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquetes);

        listEnquetes = findViewById(R.id.listEnquetes);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        carregarEnquetes();

        listEnquetes.setOnItemClickListener((adapterView, view, position, id) -> {
            long enqueteId = enqueteIds.get(position);

            Intent intent = new Intent(EnquetesActivity.this, QuizActivity.class);
            intent.putExtra("enquete_id", enqueteId);
            startActivity(intent);
        });
    }

    private void carregarEnquetes() {

        new Thread(() -> {

            try {


                SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                long usuarioId = prefs.getLong("usuario_id", -1);

                if (usuarioId == -1) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro: usuário não identificado.", Toast.LENGTH_SHORT).show());
                    return;
                }


                HashSet<Long> respondidas = new HashSet<>();

                URL urlResp = new URL(
                        SUPABASE_URL + "/rest/v1/avaliacoes_desempenho"
                                + "?id_usuario=eq." + usuarioId
                                + "&select=id_conteudo"
                );

                HttpURLConnection connResp = (HttpURLConnection) urlResp.openConnection();
                connResp.setRequestMethod("GET");
                connResp.setRequestProperty("apikey", SUPABASE_API_KEY);
                connResp.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                BufferedReader brR = new BufferedReader(new InputStreamReader(connResp.getInputStream()));
                StringBuilder sbR = new StringBuilder();
                String lineR;

                while ((lineR = brR.readLine()) != null) sbR.append(lineR);

                JSONArray arrResp = new JSONArray(sbR.toString());

                for (int i = 0; i < arrResp.length(); i++) {
                    JSONObject obj = arrResp.getJSONObject(i);
                    respondidas.add(obj.getLong("id_conteudo"));
                }


                URL url = new URL(
                        SUPABASE_URL + "/rest/v1/questionarios?select=id,titulo"
                );

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) sb.append(line);

                JSONArray arr = new JSONArray(sb.toString());

                enqueteIds.clear();
                enqueteTitulos.clear();


                for (int i = 0; i < arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);

                    long idConteudo = obj.getLong("id");

                    if (respondidas.contains(idConteudo)) {
                        continue;
                    }

                    enqueteIds.add(idConteudo);
                    enqueteTitulos.add(obj.getString("titulo"));
                }


                runOnUiThread(() -> {

                    if (enqueteIds.isEmpty()) {
                        Toast.makeText(this,
                                "Nenhuma enquete disponível. Todas já foram respondidas!",
                                Toast.LENGTH_LONG).show();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.item_enquete,
                            R.id.tvTitulo,
                            enqueteTitulos
                    );
                    listEnquetes.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
