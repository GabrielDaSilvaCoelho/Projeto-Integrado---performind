package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private int acertos = 0;
    private long usuarioId = -1;
    private long questionarioId = -1;
    private static final String PERGUNTA_ABERTA_PADRAO =
            "Descreva como você lida com suas atividades, responsabilidades e prazos no dia a dia.";

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
        btnNext = findViewById(R.id.btnNext);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        usuarioId = prefs.getLong("usuario_id", -1L);
        if (usuarioId == -1) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        questionarioId = getIntent().getLongExtra("enquete_id", -1L);
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

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                questoes.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    long id = o.getLong("id");
                    String enunciado = o.getString("enunciado");
                    JSONArray altJson = o.getJSONArray("alternativas");
                    ArrayList<String> alternativas = new ArrayList<>();
                    for (int j = 0; j < altJson.length(); j++) alternativas.add(altJson.getString(j));
                    int correctIndex = o.getInt("indice_correta");
                    questoes.add(new QuestionModel(id, enunciado, alternativas, correctIndex));
                }

                runOnUiThread(() -> {
                    if (!questoes.isEmpty()) mostrarQuestao();
                    else finish();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void mostrarQuestao() {
        if (index >= questoes.size()) {
            Toast.makeText(this, "Fim do quiz!", Toast.LENGTH_SHORT).show();

            int totalPerguntas = questoes.size();

            String idConteudo = String.valueOf(questionarioId);

            Intent intent = new Intent(QuizActivity.this, RespostaAbertaActivity.class);
            intent.putExtra(RespostaAbertaActivity.EXTRA_ID_USUARIO, String.valueOf(usuarioId));
            intent.putExtra(RespostaAbertaActivity.EXTRA_ID_CONTEUDO, idConteudo);
            intent.putExtra(RespostaAbertaActivity.EXTRA_TOTAL_PERGUNTAS, totalPerguntas);
            intent.putExtra(RespostaAbertaActivity.EXTRA_ACERTOS, acertos);
            intent.putExtra(RespostaAbertaActivity.EXTRA_PERGUNTA_ABERTA, PERGUNTA_ABERTA_PADRAO);

            startActivity(intent);
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
                radios[i].setVisibility(android.view.View.VISIBLE);
            } else {
                radios[i].setVisibility(android.view.View.GONE);
            }
        }
        radioGroup.clearCheck();
    }

    private void verificarResposta() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecione uma alternativa!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = findViewById(selectedId);
        QuestionModel q = questoes.get(index);
        boolean correta = selected.getText().toString()
                .equals(q.getAlternatives().get(q.getCorrectIndex()));

        if (correta) {
            acertos++;
        }

        salvarResposta(usuarioId,
                q.getId(),
                q.getAlternatives().indexOf(selected.getText().toString()),
                correta
        );

        index++;
        mostrarQuestao();
    }

    private void salvarResposta(long usuarioId, long questaoId,
                                int alternativaEscolhida, boolean correta) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/respostas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("usuario_id", usuarioId);
                json.put("questao_id", questaoId);
                json.put("alternativa_escolhida", alternativaEscolhida);
                json.put("correta", correta);
                json.put("questionario_id", questionarioId);

                conn.getOutputStream().write(json.toString().getBytes());
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
