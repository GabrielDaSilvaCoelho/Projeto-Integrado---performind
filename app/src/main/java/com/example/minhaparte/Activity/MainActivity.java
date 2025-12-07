package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;

public class MainActivity extends AppCompatActivity {

    private TextView tvBemVindo, tvTipo;
    private Button btnCriarUsuario, btnEditor, btnEnquetes, btnFeed,
            btnUpload, btnListaDesempenho, btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        // Views do layout
        tvBemVindo = findViewById(R.id.tvBemVindo);
        tvTipo = findViewById(R.id.tvTipoUsuario);

        btnCriarUsuario = findViewById(R.id.btnCriarUsuario);
        btnEditor = findViewById(R.id.btnEditor);
        btnEnquetes = findViewById(R.id.btnEnquetes);
        btnFeed = findViewById(R.id.btnFeed);
        btnUpload = findViewById(R.id.btnUpload);
        btnListaDesempenho = findViewById(R.id.btnListaDesempenhoColab);
        btnSair = findViewById(R.id.btnSair);

        // Lê dados salvos no login
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        long usuarioId = prefs.getLong("usuario_id", -1L);
        String nome = prefs.getString("nome", "-");
        String tipo = prefs.getString("tipo_usuario", "").trim();

        Log.d("MAIN", "usuarioId=" + usuarioId + " tipo=" + tipo);

        // Se não tiver usuário logado, volta pro login
        if (usuarioId == -1L || tipo.isEmpty()) {
            Toast.makeText(this, "Sessão inválida. Faça login novamente.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Mostra infos básicas
        tvBemVindo.setText("Bem-vindo, " + nome);
        tvTipo.setText("Tipo de usuário: " + tipo);

        // Navegação para cada Activity

        // Criar usuário
        btnCriarUsuario.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CriarUsuarioActivity.class);
            startActivity(i);
        });

        // Editor (vídeos / conteúdo)
        btnEditor.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, EditorActivity.class);
            startActivity(i);
        });

        // Enquetes
        btnEnquetes.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, EnquetesActivity.class);
            startActivity(i);
        });

        // Feed
        btnFeed.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(i);
        });

        // Upload
        btnUpload.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(i);
        });

        // Lista de desempenho dos colaboradores
        // (Activity que você criou para listar apenas "Colaborador")
        btnListaDesempenho.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ListaDesempenhoActivity.class);
            startActivity(i);
        });

        // Sair (logout)
        btnSair.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
