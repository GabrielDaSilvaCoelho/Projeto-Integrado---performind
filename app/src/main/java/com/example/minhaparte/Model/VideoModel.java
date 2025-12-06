package com.example.minhaparte.Model;
public class VideoModel {
    public Long id;  // com L maiúsculo = permite null
    public String title;
    public String description;
    public String video_url;
    public String thumb_url;

    public VideoModel(Long id, String title, String description, String video_url, String thumb_url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video_url = video_url;
        this.thumb_url = thumb_url;
    }
}
