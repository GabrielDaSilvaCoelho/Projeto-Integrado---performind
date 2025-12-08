package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_ACTIVITY";
    private EditText editMatricula, editSenha;
    private Button btnLogin;

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";

    private static final String SUPABASE_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editMatricula = findViewById(R.id.editMatricula);
        editSenha = findViewById(R.id.editSenha);
        btnLogin = findViewById(R.id.btnLogin);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        btnLogin.setOnClickListener(v -> {
            String matricula = editMatricula.getText().toString().trim();
            String senha = editSenha.getText().toString();

            if (matricula.isEmpty() || senha.trim().isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }
            realizarLogin();
        });
    }

    private boolean validarSenhaPBKDF2ouSHA256(String senhaDigitada, String hashSalvo) {
        try {
            if (hashSalvo == null) return false;
            hashSalvo = hashSalvo.trim();

            if (hashSalvo.startsWith("v1:")) {
                String[] parts = hashSalvo.split(":");
                if (parts.length != 4) return false;

                int iteracoes = Integer.parseInt(parts[1]);
                byte[] salt = Base64.decode(parts[2], Base64.NO_WRAP);
                byte[] hashBanco = Base64.decode(parts[3], Base64.NO_WRAP);

                PBEKeySpec spec = new PBEKeySpec(
                        senhaDigitada.toCharArray(),
                        salt,
                        iteracoes,
                        hashBanco.length * 8
                );
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
                byte[] hashGerado = skf.generateSecret(spec).getEncoded();

                return MessageDigest.isEqual(hashBanco, hashGerado);
            }

            if (hashSalvo.matches("^[0-9a-fA-F]{64}$")) {
                String sha = sha256Hex(senhaDigitada);
                if (sha == null) return false;
                return sha.equalsIgnoreCase(hashSalvo);
            }

            return false;

        } catch (Exception e) {
            Log.e(TAG, "Erro ao validar senha", e);
            return false;
        }
    }

    private void realizarLogin() {
        String matricula = editMatricula.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String encodedMatricula = URLEncoder.encode(matricula, "UTF-8");
                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?matricula=eq." + encodedMatricula + "&select=*");

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
                    if (jsonArray.length() == 0) {
                        runOnUiThread(() -> Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    String senhaSalva = jsonArray.getJSONObject(0).optString("senha", null);
                    boolean ok = validarSenhaPBKDF2ouSHA256(senha, senhaSalva);

                    if (!ok) {
                        runOnUiThread(() -> Toast.makeText(this, "Senha incorreta.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    String nome = jsonArray.getJSONObject(0).optString("nome", "");
                    String matriculaRet = jsonArray.getJSONObject(0).optString("matricula", "");
                    String cpf = jsonArray.getJSONObject(0).optString("cpf", "");
                    String tipo = jsonArray.getJSONObject(0).optString("tipo", "");

                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nome", nome);
                    editor.putString("matricula", matriculaRet);
                    editor.putString("cpf", cpf);
                    editor.putString("tipo", tipo);
                    editor.apply();

                    runOnUiThread(() -> {

                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                        // 🚀 NOVO: Roteamento por tipo
                        if (tipo.equalsIgnoreCase("Colaborador")) {
                            startActivity(new Intent(LoginActivity.this, FeedActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        finish();
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Erro HTTP: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao logar", e);
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private static String sha256Hex(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "Erro SHA256", e);
            return null;
        }
    }
}