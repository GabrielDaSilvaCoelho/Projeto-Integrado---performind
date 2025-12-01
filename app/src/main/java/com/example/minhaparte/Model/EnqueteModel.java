package com.example.minhaparte.Model;

public class EnqueteModel {
    private long id;
    private String titulo;
    public EnqueteModel(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
}
