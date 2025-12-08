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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";
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

        btnLogin.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {

        String matricula = editMatricula.getText().toString().trim();
        String senhaDigitada = editSenha.getText().toString().trim();

        if (matricula.isEmpty() || senhaDigitada.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String encodedMatricula = URLEncoder.encode(matricula, "UTF-8");

                String urlStr = SUPABASE_URL
                        + "/rest/v1/usuarios?matricula=eq." + encodedMatricula
                        + "&select=*";

                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    mostrar("Erro HTTP: " + responseCode);
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                if (arr.length() == 0) {
                    mostrar("Usuário não encontrado.");
                    return;
                }

                JSONObject obj = arr.getJSONObject(0);

                String hashSalvo = obj.optString("senha", null);

                boolean ok = validarPBKDF2(senhaDigitada, hashSalvo);

                if (!ok) {
                    mostrar("Senha incorreta.");
                    return;
                }

                salvarUsuario(obj);
                redirecionar(obj.optString("tipo", ""));

            } catch (Exception e) {
                Log.e(TAG, "Erro ao logar", e);
                mostrar("Erro: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
    private boolean validarPBKDF2(String senhaDigitada, String hashCompleto) {

        try {
            if (hashCompleto == null || !hashCompleto.startsWith("v1:"))
                return false;
            String[] parts = hashCompleto.split(":");
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

        } catch (Exception e) {
            Log.e(TAG, "Erro PBKDF2", e);
            return false;
        }
    }
    private void salvarUsuario(JSONObject obj) {
        try {
            SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putLong("usuario_id", obj.getLong("id"));
            editor.putString("nome", obj.optString("nome", "-"));
            editor.putString("matricula", obj.optString("matricula", "-"));
            editor.putString("cpf", obj.optString("cpf", "-"));
            editor.putString("tipo_usuario", obj.optString("tipo", "-"));

            editor.apply();

        } catch (Exception e) {
            Log.e(TAG, "Erro salvar usuario", e);
        }
    }
    private void redirecionar(String tipoRaw) {

        String tipo = tipoRaw.trim().toLowerCase();

        Intent intent;

        switch (tipo) {
            case "colaborador":
                intent = new Intent(this, FeedActivity.class);
                break;

            case "rh":
                intent = new Intent(this, MainActivity.class);
                break;

            case "supervisor":
                intent = new Intent(this, ListaDesempenhoActivity.class);
                break;

            default:
                intent = new Intent(this, FeedActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
    private void mostrar(String msg) {
        runOnUiThread(() ->
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show()
        );
    }
}