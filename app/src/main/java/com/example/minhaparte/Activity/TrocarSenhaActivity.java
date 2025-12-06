package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class TrocarSenhaActivity extends AppCompatActivity {
    private EditText editMatricula, editSenhaAtual, editNovaSenha;
    private Button btnTrocar;
    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trocar_senha);

        editMatricula = findViewById(R.id.editMatricula);
        editSenhaAtual = findViewById(R.id.editSenhaAtual);
        editNovaSenha = findViewById(R.id.editNovaSenha);
        btnTrocar = findViewById(R.id.btnTrocar);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        btnTrocar.setOnClickListener(v -> trocarSenha());
    }

    private void trocarSenha() {
        String matricula = editMatricula.getText().toString().trim();
        String senhaAtual = editSenhaAtual.getText().toString().trim();
        String novaSenha = editNovaSenha.getText().toString().trim();

        if (matricula.isEmpty() || senhaAtual.isEmpty() || novaSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                    JSONArray array = new JSONArray(sb.toString());
                    if (array.length() == 0) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Matrícula não encontrada.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    JSONObject usuario = array.getJSONObject(0);
                    int usuarioId = usuario.getInt("id");
                    String senhaBancoHash = usuario.getString("senha").trim();

                    if (!senhaBancoHash.equals(sha256Hex(senhaAtual))) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Senha atual incorreta.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // JSON apenas com a senha
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("senha", sha256Hex(novaSenha));

                    URL updateUrl = new URL(SUPABASE_URL + "/rest/v1/usuarios?id=eq." + usuarioId);
                    HttpURLConnection updateConn = (HttpURLConnection) updateUrl.openConnection();
                    updateConn.setRequestMethod("PATCH");
                    updateConn.setRequestProperty("apikey", SUPABASE_API_KEY);
                    updateConn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                    updateConn.setRequestProperty("Content-Type", "application/json");
                    updateConn.setRequestProperty("Prefer", "return=representation"); // importante
                    updateConn.setDoOutput(true);

                    try (OutputStream os = updateConn.getOutputStream()) {
                        os.write(jsonBody.toString().getBytes("UTF-8"));
                        os.flush();
                    }

                    int updateCode = updateConn.getResponseCode();
                    if (updateCode == 204 || updateCode == 200) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Erro ao alterar senha: " + updateCode, Toast.LENGTH_SHORT).show());
                    }
                    updateConn.disconnect();

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro ao buscar usuário: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e("TROCAR_SENHA", "Erro", e);
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
