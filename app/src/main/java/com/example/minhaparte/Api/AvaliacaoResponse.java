package com.example.minhaparte.Api;

import java.util.Map;

public class AvaliacaoResponse {
    private String id_usuario;
    private String id_conteudo;

    private int classe_bert_id;
    private String classe_bert_label;

    private Map<String, Double> probabilidades;

    private int total_perguntas;
    private int acertos;

    private double score_bert;
    private double score_quiz;
    private double score_final;
    private int classe_final;

    public String getId_usuario() { return id_usuario; }
    public String getId_conteudo() { return id_conteudo; }
    public int getClasse_bert_id() { return classe_bert_id; }
    public String getClasse_bert_label() { return classe_bert_label; }
    public Map<String, Double> getProbabilidades() { return probabilidades; }
    public int getTotal_perguntas() { return total_perguntas; }
    public int getAcertos() { return acertos; }
    public double getScore_bert() { return score_bert; }
    public double getScore_quiz() { return score_quiz; }
    public double getScore_final() { return score_final; }
    public int getClasse_final() { return classe_final; }
}
