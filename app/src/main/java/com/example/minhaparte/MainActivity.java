package com.example.uploadvideoactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R; // <- import correto
import com.example.minhaparte.UploadVideoActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnGoUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // <- apenas R.layout

        btnGoUpload = findViewById(R.id.btnGoUpload);
        btnGoUpload.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadVideoActivity.class);
            startActivity(intent);
        });
    }
}
