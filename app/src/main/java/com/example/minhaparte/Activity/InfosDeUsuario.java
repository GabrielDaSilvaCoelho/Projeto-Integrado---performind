package com.example.minhaparte.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.R;

public class InfosDeUsuario extends AppCompatActivity {

    private static final String PREFS_NAME = "APP_PREFS"; // mesmo nome usado no LoginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infos_de_usuario);

        // TextViews do layout
        TextView tvNome = findViewById(R.id.tvNome);
        TextView tvMatricula = findViewById(R.id.tvMatricula);
        TextView tvCpf = findViewById(R.id.tvCpf);
        TextView tvTipo = findViewById(R.id.tvTipo);

        // Carregar SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String nome = prefs.getString("nome", "Não disponível");
        String matricula = prefs.getString("matricula", "Não informado");
        String cpf = prefs.getString("cpf", "Não informado");
        String tipo = prefs.getString("tipo_usuario", "Não informado");

        // Preencher TextViews
        tvNome.setText("Nome: " + nome);
        tvMatricula.setText("Matrícula: " + matricula);
        tvCpf.setText("CPF: " + cpf);
        tvTipo.setText("Tipo de conta: " + tipo);

        Button btnTrocarSenha = findViewById(R.id.btnTrocarSenha);
        btnTrocarSenha.setOnClickListener(v -> {
            // abrir futuro trocar senha
        });
    }
}
