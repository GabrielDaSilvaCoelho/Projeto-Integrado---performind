package com.example.minhaparte;

public class VideoModel {
    private int id;
    private String file_name;
    private String url;

    public VideoModel(int id, String file_name, String url) {
        this.id = id;
        this.file_name = file_name;
        this.url = url;
    }

    public int getId() { return id; }
    public String getFile_name() { return file_name; }
    public String getUrl() { return url; }
}
