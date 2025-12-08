package com.example.minhaparte.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;
import com.example.minhaparte.View.ScoreGaugeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView tvNome, tvMatricula, tvCargo, tvSetor, tvCpf;
    private TextView tvQtdRespondidas, tvQtdAcertos;
    private TextView tvScoreValor, tvScoreMax;
    private ScoreGaugeView gaugeScore;

    private long usuarioId = -1;

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        tvNome = findViewById(R.id.tvNome);
        tvMatricula = findViewById(R.id.tvMatricula);
        tvCargo = findViewById(R.id.tvCargo);
        tvSetor = findViewById(R.id.tvSetor);
        tvCpf = findViewById(R.id.tvCpf);
        tvQtdRespondidas = findViewById(R.id.tvQtdRespondidas);
        tvQtdAcertos = findViewById(R.id.tvQtdAcertos);

        tvScoreValor = findViewById(R.id.tvScoreValor);
        tvScoreMax = findViewById(R.id.tvScoreMax);
        gaugeScore = findViewById(R.id.gaugeScore);
        gaugeScore.setMaxScore(1000);


        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        usuarioId = prefs.getLong("usuario_id", -1L);

        if (usuarioId == -1L) {
            Toast.makeText(this, "Usuário não logado!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        carregarDadosUsuario();
        carregarEstatisticas();
        carregarScoreFinal();
    }


    private void carregarDadosUsuario() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?id=eq." + usuarioId +
                        "&select=id,nome,matricula,cargo,setor,cpf");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                int code = conn.getResponseCode();
                if (code / 100 != 2) {

                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream())
                    );
                    StringBuilder errSb = new StringBuilder();
                    String line;
                    while ((line = errReader.readLine()) != null) errSb.append(line);
                    errReader.close();
                    throw new RuntimeException("HTTP " + code + " - " + errSb.toString());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                if (arr.length() == 0) {
                    runOnUiThread(() ->
                            Toast.makeText(PerfilUsuarioActivity.this,
                                    "Usuário não encontrado no banco.",
                                    Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                JSONObject user = arr.getJSONObject(0);
                String nome = user.optString("nome", "-");
                String matricula = user.optString("matricula", "-");
                String cargo = user.optString("cargo", "-");
                String setor = user.optString("setor", "-");
                String cpf = user.optString("cpf", "-");

                runOnUiThread(() -> {
                    tvNome.setText("Nome: " + nome);
                    tvMatricula.setText("Matrícula: " + matricula);
                    tvCargo.setText("Cargo: " + cargo);
                    tvSetor.setText("Setor: " + setor);
                    tvCpf.setText("CPF: " + cpf);
                });

            } catch (Exception e) {
                e.printStackTrace();
                String msg = e.getMessage();
                runOnUiThread(() ->
                        Toast.makeText(PerfilUsuarioActivity.this,
                                "Erro ao carregar dados do usuário: " + msg,
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    /**
     * Busca estatísticas na tabela "respostas":
     * - total de respostas (conta linhas)
     * - total de acertos (correta = true)
     */
    private void carregarEstatisticas() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/respostas?usuario_id=eq." + usuarioId +
                        "&select=correta");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                int code = conn.getResponseCode();
                if (code / 100 != 2) {
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream())
                    );
                    StringBuilder errSb = new StringBuilder();
                    String line;
                    while ((line = errReader.readLine()) != null) errSb.append(line);
                    errReader.close();
                    throw new RuntimeException("HTTP " + code + " - " + errSb.toString());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                int totalRespondidas = arr.length();
                int totalAcertos = 0;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject resp = arr.getJSONObject(i);
                    if (!resp.isNull("correta") && resp.getBoolean("correta")) {
                        totalAcertos++;
                    }
                }

                int finalTotalRespondidas = totalRespondidas;
                int finalTotalAcertos = totalAcertos;

                runOnUiThread(() -> {
                    tvQtdRespondidas.setText("Perguntas respondidas: " + finalTotalRespondidas);
                    tvQtdAcertos.setText("Perguntas corretas: " + finalTotalAcertos);
                });

            } catch (Exception e) {
                e.printStackTrace();
                String msg = e.getMessage();
                runOnUiThread(() ->
                        Toast.makeText(PerfilUsuarioActivity.this,
                                "Erro ao carregar estatísticas: " + msg,
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    /**
     * Busca o score_final mais recente na tabela "avaliacoes_desempenho"
     * e atualiza o gauge (0–1000).
     */
    private void carregarScoreFinal() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = SUPABASE_URL +
                        "/rest/v1/avaliacoes_desempenho?id_usuario=eq." + usuarioId +
                        "&select=score_final&order=created_at.desc&limit=1";

                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                int code = conn.getResponseCode();
                if (code / 100 != 2) {
                    // Lê o erro retornado pelo Supabase (404, 401, etc.)
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream())
                    );
                    StringBuilder errSb = new StringBuilder();
                    String line;
                    while ((line = errReader.readLine()) != null) errSb.append(line);
                    errReader.close();

                    throw new RuntimeException("HTTP " + code + " - " + errSb.toString());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                if (arr.length() == 0) {
                    // Sem score ainda -> 0/1000
                    runOnUiThread(() -> {
                        gaugeScore.setScore(0);
                        tvScoreValor.setText("0");
                        tvScoreMax.setText("/1000");
                    });
                    return;
                }

                JSONObject obj = arr.getJSONObject(0);
                double scoreFinal = obj.optDouble("score_final", 0.0); // 0–1
                if (scoreFinal < 0) scoreFinal = 0;
                if (scoreFinal > 1) scoreFinal = 1;

                int scoreMil = (int) Math.round(scoreFinal * 1000.0);

                int finalScoreMil = scoreMil;
                runOnUiThread(() -> {
                    gaugeScore.setScore(finalScoreMil);
                    tvScoreValor.setText(String.valueOf(finalScoreMil));
                    tvScoreMax.setText("/1000");
                });

            } catch (Exception e) {
                e.printStackTrace();
                String msg = e.getMessage();
                runOnUiThread(() -> {
                    gaugeScore.setScore(0);
                    tvScoreValor.setText("0");
                    tvScoreMax.setText("/1000");
                    Toast.makeText(PerfilUsuarioActivity.this,
                            "Erro ao carregar score: " + msg,
                            Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
