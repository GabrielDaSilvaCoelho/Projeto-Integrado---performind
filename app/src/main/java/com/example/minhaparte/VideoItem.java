package com.example.minhaparte;

public class VideoItem {
    private String titulo;
    private String url;

    public VideoItem(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }
}
