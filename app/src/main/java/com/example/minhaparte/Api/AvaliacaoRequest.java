package com.example.minhaparte.Api;

import com.google.gson.annotations.SerializedName;

public class AvaliacaoRequest {

    @SerializedName("id_usuario")
    private String idUsuario;

    @SerializedName("id_conteudo")
    private String idConteudo;

    @SerializedName("pergunta_aberta")
    private String perguntaAberta;

    @SerializedName("resposta_aberta")
    private String respostaAberta;

    @SerializedName("total_perguntas")
    private int totalPerguntas;

    @SerializedName("acertos")
    private int acertos;

    public AvaliacaoRequest(String idUsuario,
                            String idConteudo,
                            String perguntaAberta,
                            String respostaAberta,
                            int totalPerguntas,
                            int acertos) {
        this.idUsuario = idUsuario;
        this.idConteudo = idConteudo;
        this.perguntaAberta = perguntaAberta;
        this.respostaAberta = respostaAberta;
        this.totalPerguntas = totalPerguntas;
        this.acertos = acertos;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdConteudo() {
        return idConteudo;
    }

    public String getPerguntaAberta() {
        return perguntaAberta;
    }

    public String getRespostaAberta() {
        return respostaAberta;
    }

    public int getTotalPerguntas() {
        return totalPerguntas;
    }

    public int getAcertos() {
        return acertos;
    }
}
