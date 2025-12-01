package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.Model.QuestionModel;
import com.example.minhaparte.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    private EditText etTitulo, etQuestion, etAlt1, etAlt2, etAlt3, etAlt4, etCorrect;
    private Button btnSalvar, btnProxima;
    private ArrayList<QuestionModel> listaQuestoes = new ArrayList<>();
    private int questaoAtual = 1;
    private long questionarioId = -1;
    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        etTitulo = findViewById(R.id.etTitulo);
        etQuestion = findViewById(R.id.etQuestion);
        etAlt1 = findViewById(R.id.etAlt1);
        etAlt2 = findViewById(R.id.etAlt2);
        etAlt3 = findViewById(R.id.etAlt3);
        etAlt4 = findViewById(R.id.etAlt4);
        etCorrect = findViewById(R.id.etCorrect);
        btnProxima = findViewById(R.id.btnProxima);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnProxima.setOnClickListener(v -> adicionarQuestao());
        btnSalvar.setOnClickListener(v -> salvarQuestionario());
    }
    private void adicionarQuestao() {
        String pergunta = etQuestion.getText().toString().trim();
        if (pergunta.isEmpty()) {
            Toast.makeText(this, "Digite o enunciado da questão!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> alternativas = new ArrayList<>();
        alternativas.add(etAlt1.getText().toString());
        alternativas.add(etAlt2.getText().toString());
        alternativas.add(etAlt3.getText().toString());
        alternativas.add(etAlt4.getText().toString());

        int correta;
        try {
            correta = Integer.parseInt(etCorrect.getText().toString()) - 1;
        } catch (Exception e) {
            Toast.makeText(this, "Digite o número da alternativa correta (1-4)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correta < 0 || correta >= alternativas.size()) {
            Toast.makeText(this, "Número da alternativa inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        listaQuestoes.add(new QuestionModel(pergunta, alternativas, correta));
        Toast.makeText(this, "Questão " + questaoAtual + " adicionada!", Toast.LENGTH_SHORT).show();
        questaoAtual++;

        limparCampos();
    }

    private void limparCampos() {
        etQuestion.setText("");
        etAlt1.setText("");
        etAlt2.setText("");
        etAlt3.setText("");
        etAlt4.setText("");
        etCorrect.setText("");
    }

    private long buscarUsuarioId(String nomeUsuario) {
        final long[] usuarioId = {-1};

        try {
            URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?nome=eq." + nomeUsuario + "&select=id");
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
            if (arr.length() > 0) {
                usuarioId[0] = arr.getJSONObject(0).getLong("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarioId[0];
    }
    private void salvarQuestionario() {
        if (listaQuestoes.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos uma questão!", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = etTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Digite o título do questionário!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // 🔹 Busca ID do usuário
                long usuarioId = buscarUsuarioId("admins");
                if (usuarioId == -1) {
                    runOnUiThread(() ->
                            Toast.makeText(EditorActivity.this, "Usuário não encontrado!", Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                URL url = new URL(SUPABASE_URL + "/rest/v1/questionarios");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("usuario_id", usuarioId);
                json.put("titulo", titulo);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                if (code != 201) {
                    runOnUiThread(() -> Toast.makeText(EditorActivity.this,
                            "Erro ao criar questionário: HTTP " + code, Toast.LENGTH_LONG).show());
                    return;
                }

                JSONArray arr = new JSONArray(new java.util.Scanner(conn.getInputStream()).useDelimiter("\\A").next());
                questionarioId = arr.getJSONObject(0).getLong("id");
                Log.d("SUPABASE", "Questionário criado: ID=" + questionarioId);

                conn.disconnect();

                int sucesso = 0;
                for (QuestionModel q : listaQuestoes) {
                    boolean ok = QuestaoService.salvarQuestaoComQuestionario(
                            questionarioId,
                            usuarioId,
                            q.getQuestionText(),
                            q.getAlternatives().toArray(new String[0]),
                            q.getCorrectIndex()
                    );
                    if (ok) sucesso++;
                }

                int finalSucesso = sucesso;
                runOnUiThread(() -> {
                    if (finalSucesso == listaQuestoes.size()) {
                        Toast.makeText(EditorActivity.this, "Questionário e questões salvos com sucesso!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditorActivity.this,
                                "Algumas questões falharam (" + finalSucesso + "/" + listaQuestoes.size() + ")", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(EditorActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
