package com.example.minhaparte;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnCriarUsuario, btnEditor, btnFeed, btnQuiz, btnUpload, btnEnquete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        // === Inicialização dos botões existentes ===
        btnCriarUsuario = findViewById(R.id.btnCriarUsuario);
        btnEditor = findViewById(R.id.btnEditor);
        btnFeed = findViewById(R.id.btnFeed);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnUpload = findViewById(R.id.btnUpload);

        // === Novo botão: Enquete ===
        btnEnquete = findViewById(R.id.btnEnquete);

        // === Ações dos botões ===
        btnCriarUsuario.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CriarUsuarioActivity.class)));

        btnEditor.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EditorActivity.class)));

        btnFeed.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FeedActivity.class)));

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QuizActivity.class)));

        btnUpload.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UploadActivity.class)));

        // === Ação do botão Enquete ===
        btnEnquete.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EnquetesActivity.class)));
    }
}
