package com.example.minhaparte;

public class VideoModel {
    public String title;
    public String description;
    public String video_url;
    public String thumb_url;

    public VideoModel(String title, String description, String video_url, String thumb_url) {
        this.title = title;
        this.description = (description == null || description.isEmpty()) ? "Sem descrição" : description;
        this.video_url = video_url;
        this.thumb_url = thumb_url;
    }
}
