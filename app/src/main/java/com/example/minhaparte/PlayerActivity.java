package com.example.minhaparte;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        VideoView videoView = findViewById(R.id.videoView);
        String videoUrl = getIntent().getStringExtra("videoUrl");

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();
    }
}
