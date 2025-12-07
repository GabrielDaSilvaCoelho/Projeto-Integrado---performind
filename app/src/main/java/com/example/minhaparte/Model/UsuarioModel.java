package com.example.minhaparte.Model;

import com.google.gson.annotations.SerializedName;

public class UsuarioModel {
    public long id;
    public String nome;
    public String matricula;
    public String senha;
    public String tipo;
    public String cargo;
    public String setor;
    public String cpf;
    public String contato;

    @SerializedName("score_final")
    public Double score;
}
