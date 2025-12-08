package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {

    private EditText etTitulo, etQuestion, etAlt1, etAlt2, etAlt3, etAlt4, etCorrect;
    private Button btnSalvar, btnProxima;
    private Switch switchDiscursiva;
    private LinearLayout layoutAlternativas;


    private static class LocalQuestion {
        String enunciado;
        ArrayList<String> alternativas;
        int indiceCorreta;
        boolean discursiva;

        LocalQuestion(String enunciado, ArrayList<String> alternativas, int indiceCorreta, boolean discursiva) {
            this.enunciado = enunciado;
            this.alternativas = alternativas;
            this.indiceCorreta = indiceCorreta;
            this.discursiva = discursiva;
        }
    }

    private ArrayList<LocalQuestion> listaQuestoes = new ArrayList<>();
    private int numeroQuestao = 1;
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

        switchDiscursiva = findViewById(R.id.switchDiscursiva);
        layoutAlternativas = findViewById(R.id.layoutAlternativas);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));
        
        switchDiscursiva.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutAlternativas.setVisibility(View.GONE);
            } else {
                layoutAlternativas.setVisibility(View.VISIBLE);
            }
        });

        btnProxima.setOnClickListener(v -> adicionarQuestao());
        btnSalvar.setOnClickListener(v -> salvarQuestionario());
    }

    private void adicionarQuestao() {
        String pergunta = etQuestion.getText().toString().trim();
        if (pergunta.isEmpty()) {
            Toast.makeText(this, "Digite o enunciado da questão!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDiscursiva = switchDiscursiva.isChecked();

        if (isDiscursiva) {

            ArrayList<String> alternativasVazias = new ArrayList<>();
            int indiceCorreta = -1;

            listaQuestoes.add(new LocalQuestion(pergunta, alternativasVazias, indiceCorreta, true));
            Toast.makeText(this,
                    "Questão discursiva " + numeroQuestao + " adicionada!",
                    Toast.LENGTH_SHORT).show();
            numeroQuestao++;
            limparCampos();
            return;
        }

        // Questão objetiva
        ArrayList<String> alternativas = new ArrayList<>();
        alternativas.add(etAlt1.getText().toString().trim());
        alternativas.add(etAlt2.getText().toString().trim());
        alternativas.add(etAlt3.getText().toString().trim());
        alternativas.add(etAlt4.getText().toString().trim());

        int preenchidas = 0;
        for (String alt : alternativas) {
            if (!alt.isEmpty()) preenchidas++;
        }
        if (preenchidas < 2) {
            Toast.makeText(this, "Preencha pelo menos duas alternativas.", Toast.LENGTH_SHORT).show();
            return;
        }

        int correta;
        try {
            correta = Integer.parseInt(etCorrect.getText().toString()) - 1;
        } catch (Exception e) {
            Toast.makeText(this, "Digite o número da alternativa correta (1-4)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correta < 0 || correta >= alternativas.size()) {
            Toast.makeText(this, "Número da alternativa correta inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        listaQuestoes.add(new LocalQuestion(pergunta, alternativas, correta, false));
        Toast.makeText(this,
                "Questão objetiva " + numeroQuestao + " adicionada!",
                Toast.LENGTH_SHORT).show();
        numeroQuestao++;

        limparCampos();
    }

    private void limparCampos() {
        etQuestion.setText("");
        etAlt1.setText("");
        etAlt2.setText("");
        etAlt3.setText("");
        etAlt4.setText("");
        etCorrect.setText("");
        switchDiscursiva.setChecked(false);
        layoutAlternativas.setVisibility(View.VISIBLE);
    }

    private long buscarUsuarioId(String nomeUsuario) {
        long usuarioId = -1;
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
                usuarioId = arr.getJSONObject(0).getLong("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarioId;
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
                long usuarioId = buscarUsuarioId("admins");
                if (usuarioId == -1) {
                    runOnUiThread(() ->
                            Toast.makeText(EditorActivity.this,
                                    "Usuário 'admins' não encontrado!",
                                    Toast.LENGTH_LONG).show());
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
                            "Erro ao criar questionário: HTTP " + code,
                            Toast.LENGTH_LONG).show());
                    return;
                }

                JSONArray arr = new JSONArray(
                        new java.util.Scanner(conn.getInputStream())
                                .useDelimiter("\\A").next()
                );
                questionarioId = arr.getJSONObject(0).getLong("id");
                Log.d("SUPABASE", "Questionário criado: ID=" + questionarioId);

                conn.disconnect();

                int sucesso = 0;
                for (LocalQuestion q : listaQuestoes) {

                    boolean ok = QuestaoService.salvarQuestaoComQuestionario(
                            questionarioId,
                            usuarioId,
                            q.enunciado,
                            q.alternativas.toArray(new String[0]),
                            q.indiceCorreta
                    );
                    if (ok) sucesso++;
                }

                int finalSucesso = sucesso;
                runOnUiThread(() -> {
                    if (finalSucesso == listaQuestoes.size()) {
                        Toast.makeText(EditorActivity.this,
                                "Questionário e questões salvos com sucesso!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditorActivity.this,
                                "Algumas questões falharam (" + finalSucesso + "/" + listaQuestoes.size() + ")",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(EditorActivity.this,
                                "Erro: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
