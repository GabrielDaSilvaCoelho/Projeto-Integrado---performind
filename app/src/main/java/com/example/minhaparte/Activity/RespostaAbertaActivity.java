package com.example.minhaparte.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// IMPORTS CORRETOS
import com.example.minhaparte.Api.ApiClient;
import com.example.minhaparte.Api.FlaskApiService;
import com.example.minhaparte.Api.AvaliacaoRequest;
import com.example.minhaparte.Api.AvaliacaoResponse;

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

    private String idUsuario;
    private String idConteudo;
    private int totalPerguntas;
    private int acertos;
    private String perguntaAberta;

    // TIPO CORRETO
    private FlaskApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resposta_aberta);

        etRespostaAberta = findViewById(R.id.etRespostaAberta);
        tvResultadoIA = findViewById(R.id.tvResultadoIA);
        tvPerguntaAberta = findViewById(R.id.tvPerguntaAberta);
        Button btnEnviar = findViewById(R.id.btnEnviarResposta);

        // Recupera dados vindos do QuizActivity
        idUsuario = getIntent().getStringExtra(EXTRA_ID_USUARIO);
        idConteudo = getIntent().getStringExtra(EXTRA_ID_CONTEUDO);
        totalPerguntas = getIntent().getIntExtra(EXTRA_TOTAL_PERGUNTAS, 0);
        acertos = getIntent().getIntExtra(EXTRA_ACERTOS, 0);
        perguntaAberta = getIntent().getStringExtra(EXTRA_PERGUNTA_ABERTA);

        if (perguntaAberta != null && !perguntaAberta.isEmpty()) {
            tvPerguntaAberta.setText(perguntaAberta);
        }

        // AQUI ESTAVA O ERRO — CORRIGIDO
        apiService = ApiClient.getApiService();

        btnEnviar.setOnClickListener(v -> enviarParaIA());
    }

    private void enviarParaIA() {
        String resposta = etRespostaAberta.getText().toString().trim();

        if (resposta.isEmpty()) {
            Toast.makeText(this, "Digite sua resposta.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Monta o JSON da requisição
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

                    // Mensagem de sucesso
                    Toast.makeText(RespostaAbertaActivity.this,
                            "Avaliação enviada com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    // 🔥 Fecha esta Activity e retorna para a lista
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
}
