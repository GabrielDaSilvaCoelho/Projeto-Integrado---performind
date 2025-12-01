package com.example.minhaparte.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.Model.QuestionModel;
import com.example.minhaparte.R;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private Button btnNext;

    private ArrayList<QuestionModel> questoes = new ArrayList<>();
    private int index = 0;
    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    private long usuarioId = 1;
    private long questionarioId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Erro: usuário não logado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        questionarioId = getIntent().getLongExtra("enquete_id", -1);

        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
        btnNext = findViewById(R.id.btnNext);
        carregarQuestoes();
        btnNext.setOnClickListener(v -> verificarResposta());
    }
    private void carregarQuestoes() {

        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/questoes?questionario_id=eq." + questionarioId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                Log.d("SUPABASE", "GET questoes -> questionario_id=" + questionarioId);

                int responseCode = conn.getResponseCode();
                Log.d("SUPABASE", "HTTP CODE QUESTOES = " + responseCode);

                if (responseCode != 200) {
                    runOnUiThread(() -> Toast.makeText(this, "Erro HTTP: " + responseCode, Toast.LENGTH_LONG).show());
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null)
                    sb.append(line);

                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                questoes.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);

                    long id = o.optLong("id");
                    String enunciado = o.optString("enunciado");

                    JSONArray alternativasJson = o.getJSONArray("alternativas");
                    ArrayList<String> alternativas = new ArrayList<>();
                    for (int j = 0; j < alternativasJson.length(); j++)
                        alternativas.add(alternativasJson.getString(j));

                    int indiceCorreta = o.optInt("indice_correta");

                    questoes.add(new QuestionModel(id, enunciado, alternativas, indiceCorreta));
                }

                runOnUiThread(() -> {
                    if (questoes.isEmpty()) {
                        Toast.makeText(this, "Nenhuma questão encontrada!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        mostrarQuestao();
                    }
                });

            } catch (Exception e) {
                Log.e("SUPABASE", "Erro ao carregar questoes", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro ao carregar: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private void mostrarQuestao() {
        if (index >= questoes.size()) {
            Toast.makeText(this, "Fim do quiz!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        QuestionModel q = questoes.get(index);
        tvQuestion.setText(q.getQuestionText());

        ArrayList<String> alternativas = new ArrayList<>(q.getAlternatives());
        Collections.shuffle(alternativas);

        RadioButton[] radios = {rb1, rb2, rb3, rb4, rb5};

        for (int i = 0; i < radios.length; i++) {
            if (i < alternativas.size()) {
                radios[i].setText(alternativas.get(i));
                radios[i].setVisibility(View.VISIBLE);
            } else {
                radios[i].setVisibility(View.GONE);
            }
        }
        radioGroup.clearCheck();
    }
    private void verificarResposta() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecione uma alternativa", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = findViewById(selectedId);
        String resposta = selected.getText().toString();

        QuestionModel q = questoes.get(index);
        String respostaCerta = q.getAlternatives().get(q.getCorrectIndex());

        boolean correta = resposta.equals(respostaCerta);

        salvarResposta(usuarioId, q.getId(), q.getAlternatives().indexOf(resposta), correta);

        if (correta)
            Toast.makeText(this, "Correta!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Errada! Certa: " + respostaCerta, Toast.LENGTH_LONG).show();

        index++;
        mostrarQuestao();
    }
    private void salvarResposta(long usuarioId, long questaoId, int alternativaEscolhida, boolean correta) {
        new Thread(() -> {
            try {

                URL url = new URL(SUPABASE_URL + "/rest/v1/respostas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=minimal"); // IMPORTANTE!
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("usuario_id", usuarioId);
                body.put("questao_id", questaoId);
                body.put("alternativa_escolhida", alternativaEscolhida);
                body.put("correta", correta);
                body.put("questionario_id", questionarioId); // OBRIGATÓRIO na sua tabela

                Log.d("SUPABASE", "BODY -> " + body);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                Log.d("SUPABASE", "POST RESPOSTA HTTP = " + code);

                if (code != 201 && code != 200 && code != 204) {
                    Log.e("SUPABASE", "ERRO AO SALVAR RESPOSTA: HTTP " + code);
                }

            } catch (Exception e) {
                Log.e("SUPABASE", "Erro salvar resp.", e);
            }
        }).start();
    }
}
