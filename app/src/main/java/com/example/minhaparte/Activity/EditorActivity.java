package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.Model.QuestionModel;
import com.example.minhaparte.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final String SUPABASE_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

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
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));
        btnProxima.setOnClickListener(v -> adicionarQuestao());
        btnSalvar.setOnClickListener(v -> salvarQuestionario());
    }
    private void adicionarQuestao() {
        String pergunta = etQuestion.getText().toString().trim();
        if (pergunta.isEmpty()) {
            Toast.makeText(this, "Digite o enunciado!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Número da alternativa correta inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correta < 0 || correta > 3) {
            Toast.makeText(this, "Digite 1, 2, 3 ou 4!", Toast.LENGTH_SHORT).show();
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
    private void salvarQuestionario() {
        if (listaQuestoes.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos 1 questão!", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = etTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Digite o título!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                if (questionarioId == -1) {
                    URL url = new URL(SUPABASE_URL + "/rest/v1/questionarios");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                    conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Prefer", "return=representation");
                    conn.setDoOutput(true);

                    JSONObject body = new JSONObject();
                    body.put("usuario_id", 4);
                    body.put("titulo", titulo);

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes());
                    os.close();

                    InputStream is = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
                    String resposta = new java.util.Scanner(is).useDelimiter("\\A").next();
                    Log.d("SUPABASE", "Resposta criarQuestionario: " + resposta);

                    if (conn.getResponseCode() != 201) {
                        runOnUiThread(() -> Toast.makeText(this, "Erro ao criar questionário!", Toast.LENGTH_LONG).show());
                        return;
                    }

                    JSONArray arr = new JSONArray(resposta);
                    questionarioId = arr.getJSONObject(0).getLong("id");
                    Log.d("SUPABASE", "Questionário criado com ID: " + questionarioId);
                    conn.disconnect();
                }
                int okCount = 0;
                final int total = listaQuestoes.size();

                for (QuestionModel q : listaQuestoes) {
                    URL urlQ = new URL(SUPABASE_URL + "/rest/v1/questoes");
                    HttpURLConnection connQ = (HttpURLConnection) urlQ.openConnection();
                    connQ.setRequestMethod("POST");
                    connQ.setRequestProperty("apikey", SUPABASE_API_KEY);
                    connQ.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                    connQ.setRequestProperty("Content-Type", "application/json");
                    connQ.setRequestProperty("Prefer", "return=minimal");
                    connQ.setDoOutput(true);

                    JSONObject jsonQ = new JSONObject();
                    jsonQ.put("questionario_id", questionarioId);
                    jsonQ.put("usuario_id", 4);
                    jsonQ.put("enunciado", q.getQuestionText());
                    jsonQ.put("alternativas", new JSONArray(q.getAlternatives()));
                    jsonQ.put("indice_correta", q.getCorrectIndex());

                    OutputStream oq = connQ.getOutputStream();
                    oq.write(jsonQ.toString().getBytes());
                    oq.close();

                    InputStream isQ = connQ.getErrorStream() != null ? connQ.getErrorStream() : connQ.getInputStream();
                    String respostaQ = new java.util.Scanner(isQ).useDelimiter("\\A").next();
                    Log.d("SUPABASE", "Resposta salvarQuestao: " + respostaQ);

                    if (connQ.getResponseCode() == 201) okCount++;
                    connQ.disconnect();
                }

                final int finalOk = okCount;
                runOnUiThread(() -> {
                    if (finalOk == total)
                        Toast.makeText(this, "Questionário salvo com sucesso! (" + finalOk + "/" + total + ")", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(this, "Algumas questões não foram salvas! (" + finalOk + "/" + total + ")", Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                Log.e("SUPABASE", "Erro salvarQuestionario", e);
            }
        }).start();
    }
}
