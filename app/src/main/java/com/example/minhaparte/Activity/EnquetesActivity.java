package com.example.minhaparte.Activity;

import android.content.Intent;
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
        carregarEnquetes();
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));
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
                URL url = new URL(SUPABASE_URL + "/rest/v1/questionarios?select=id,titulo");
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
                    enqueteIds.add(obj.getLong("id"));
                    enqueteTitulos.add(obj.getString("Titulo"));
                }

                runOnUiThread(() -> {
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
