package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.Api.ApiClient;
import com.example.minhaparte.Api.AvaliacaoRequest;
import com.example.minhaparte.Api.AvaliacaoResponse;
import com.example.minhaparte.Api.FlaskApiService;
import com.example.minhaparte.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RespostaAbertaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_USUARIO = "extra_id_usuario";
    public static final String EXTRA_ID_CONTEUDO = "extra_id_conteudo";
    public static final String EXTRA_TOTAL_PERGUNTAS = "extra_total_perguntas";
    public static final String EXTRA_ACERTOS = "extra_acertos";
    public static final String EXTRA_PERGUNTA_ABERTA = "extra_pergunta_aberta";

    private EditText etRespostaAberta;
    private TextView tvResultadoIA;
    private TextView tvPerguntaAberta;
    private Button btnEnviar;

    private String idUsuario;
    private String idConteudo;
    private int totalPerguntas;
    private int acertos;
    private String perguntaAberta;

    private FlaskApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resposta_aberta);

        etRespostaAberta = findViewById(R.id.etRespostaAberta);
        tvResultadoIA = findViewById(R.id.tvResultadoIA);
        tvPerguntaAberta = findViewById(R.id.tvPerguntaAberta);
        btnEnviar = findViewById(R.id.btnEnviarResposta);

        idUsuario = getIntent().getStringExtra(EXTRA_ID_USUARIO);
        idConteudo = getIntent().getStringExtra(EXTRA_ID_CONTEUDO);
        totalPerguntas = getIntent().getIntExtra(EXTRA_TOTAL_PERGUNTAS, 0);
        acertos = getIntent().getIntExtra(EXTRA_ACERTOS, 0);
        perguntaAberta = getIntent().getStringExtra(EXTRA_PERGUNTA_ABERTA);

        if (perguntaAberta != null && !perguntaAberta.isEmpty()) {
            tvPerguntaAberta.setText(perguntaAberta);
        }

        apiService = ApiClient.getApiService();

        btnEnviar.setOnClickListener(v -> enviarParaIA());
    }

    private void enviarParaIA() {
        String resposta = etRespostaAberta.getText().toString().trim();

        if (resposta.isEmpty()) {
            Toast.makeText(this, "Digite sua resposta.", Toast.LENGTH_SHORT).show();
            return;
        }

        AvaliacaoRequest request = new AvaliacaoRequest(
                idUsuario,
                idConteudo,
                perguntaAberta != null ? perguntaAberta : "",
                resposta,
                totalPerguntas,
                acertos
        );

        tvResultadoIA.setText("Enviando para IA, aguarde...");

        apiService.avaliarDesempenho(request).enqueue(new Callback<AvaliacaoResponse>() {
            @Override
            public void onResponse(Call<AvaliacaoResponse> call, Response<AvaliacaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AvaliacaoResponse resp = response.body();

                    int classeFinal = resp.getClasse_final();
                    double scoreFinal = resp.getScore_final();
                    String classeBert = resp.getClasse_bert_label();

                    String textoResultado = "Classe final: " + classeFinal + "\n"
                            + "Score final: " + String.format("%.2f", scoreFinal) + "\n"
                            + "IA (texto): " + classeBert + "\n"
                            + "Acertos no quiz: " + acertos + "/" + totalPerguntas;

                    tvResultadoIA.setText(textoResultado);

                    marcarQuestionarioComoRespondido();
                    Toast.makeText(RespostaAbertaActivity.this, "Questionário enviado com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RespostaAbertaActivity.this, FeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    tvResultadoIA.setText("Erro na resposta da API.");
                }
            }

            @Override
            public void onFailure(Call<AvaliacaoResponse> call, Throwable t) {
                t.printStackTrace();
                tvResultadoIA.setText("Falha ao comunicar com a API.");
            }
        });
    }

    private void marcarQuestionarioComoRespondido() {
        if (idUsuario == null || idConteudo == null) return;
        SharedPreferences prefs = getSharedPreferences("questionarios", MODE_PRIVATE);
        String chave = "respondido_" + idUsuario + "_" + idConteudo;
        prefs.edit().putBoolean(chave, true).apply();
    }
}
