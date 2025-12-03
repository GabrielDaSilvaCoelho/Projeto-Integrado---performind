package com.example.minhaparte.Api;

public class AvaliacaoRequest {
    private String id_usuario;
    private String id_conteudo;
    private String pergunta;
    private String texto_resposta;
    private int total_perguntas;
    private int acertos;

    public AvaliacaoRequest(String idUsuario,
                            String idConteudo,
                            String pergunta,
                            String textoResposta,
                            int totalPerguntas,
                            int acertos) {
        this.id_usuario = idUsuario;
        this.id_conteudo = idConteudo;
        this.pergunta = pergunta;
        this.texto_resposta = textoResposta;
        this.total_perguntas = totalPerguntas;
        this.acertos = acertos;
    }
}
