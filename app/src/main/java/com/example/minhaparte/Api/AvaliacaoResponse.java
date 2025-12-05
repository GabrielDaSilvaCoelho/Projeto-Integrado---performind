package com.example.minhaparte.Api;

import com.google.gson.annotations.SerializedName;

public class AvaliacaoResponse {

    @SerializedName("id_usuario")
    private String idUsuario;

    @SerializedName("id_conteudo")
    private String idConteudo;

    // IA (texto)
    @SerializedName("classe_bert")
    private int classeBert;

    @SerializedName("classe_bert_label")
    private String classeBertLabel;

    @SerializedName("score_bert")
    private double scoreBert;

    // Quiz
    @SerializedName("total_perguntas")
    private int totalPerguntas;

    @SerializedName("acertos")
    private int acertos;

    @SerializedName("quiz_score")
    private double quizScore;

    // Final
    @SerializedName("score_final")
    private double scoreFinal;

    @SerializedName("classe_final")
    private int classeFinal;

    // GETTERS

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdConteudo() {
        return idConteudo;
    }

    public int getClasseBert() {
        return classeBert;
    }

    public String getClasse_bert_label() {
        return classeBertLabel;
    }

    public double getScore_bert() {
        return scoreBert;
    }

    public int getTotal_perguntas() {
        return totalPerguntas;
    }

    public int getAcertos() {
        return acertos;
    }

    public double getQuiz_score() {
        return quizScore;
    }

    public double getScore_final() {
        return scoreFinal;
    }

    public int getClasse_final() {
        return classeFinal;
    }
}
