package com.example.minhaparte;

public class Enquete {
    private long id;
    private String titulo;

    public Enquete(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
}
