package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.R;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText editMatricula, editSenha;
    private Button btnLogin;

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editMatricula = findViewById(R.id.editMatricula);
        editSenha = findViewById(R.id.editSenha);
        btnLogin = findViewById(R.id.btnLogin);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        btnLogin.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {
        String matricula = editMatricula.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (matricula.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String senhaHash = sha256Hex(senha);

        HashMap<String, String> loginData = new HashMap<>();
        loginData.put("matricula", matricula);
        loginData.put("senha", senhaHash);

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String encodedMatricula = URLEncoder.encode(loginData.get("matricula"), "UTF-8");
                String encodedSenha = URLEncoder.encode(loginData.get("senha"), "UTF-8");

                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?matricula=eq." + encodedMatricula + "&senha=eq." + encodedSenha + "&select=*");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();

                    JSONArray jsonArray = new JSONArray(sb.toString());
                    if (jsonArray.length() > 0) {
                        // Alterado para long
                        long usuarioId = jsonArray.getJSONObject(0).getLong("id");

                        // Salvar no SharedPreferences como long
                        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                        prefs.edit().putLong("usuario_id", usuarioId).apply();

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            // Passando via Intent como long
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("usuarioId", usuarioId);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Matrícula ou senha incorretas.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro HTTP: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("LOGIN", "Erro ao logar", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String sha256Hex(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e("HASH_ERROR", "Erro ao gerar hash", e);
            return null;
        }
    }
}
