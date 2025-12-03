package com.example.minhaparte.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;

public class MainActivity extends AppCompatActivity {

    private Button btnCriarUsuario, btnEditor, btnFeed, btnQuiz, btnUpload, btnEnquete;
    private Button btnVideos, btnListaUsuarios, btnTrocarSenha, btnRespostaAberta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        btnCriarUsuario = findViewById(R.id.btnCriarUsuario);
        btnRespostaAberta = findViewById(R.id.btnRespostaAberta);
        btnEditor = findViewById(R.id.btnEditor);
        btnFeed = findViewById(R.id.btnFeed);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnUpload = findViewById(R.id.btnUpload);
        btnEnquete = findViewById(R.id.btnEnquete);
        btnVideos = findViewById(R.id.btnvideos);
        btnListaUsuarios = findViewById(R.id.btnListaUsuarios);
        btnTrocarSenha = findViewById(R.id.btnTrocarSenha);

        btnCriarUsuario.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CriarUsuarioActivity.class)));

        btnEditor.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EditorActivity.class)));

        btnFeed.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FeedActivity.class)));

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QuizActivity.class)));

        btnRespostaAberta.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RespostaAbertaActivity.class)));

        btnUpload.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UploadActivity.class)));

        btnEnquete.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EnquetesActivity.class)));

        btnVideos.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, VideoListActivity.class)));

        btnListaUsuarios.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ListarUsuariosActivity.class)));

        btnTrocarSenha.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TrocarSenhaActivity.class)));
    }
}
