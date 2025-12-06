package com.example.minhaparte.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minhaparte.R;

public class InfosDeUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infos_de_usuario);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        TextView tvNome = findViewById(R.id.tvNome);
        TextView tvMatricula = findViewById(R.id.tvMatricula);
        TextView tvCpf = findViewById(R.id.tvCpf);
        TextView tvTipo = findViewById(R.id.tvTipo);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        String nome = prefs.getString("nome", "Não disponível");
        String matricula = prefs.getString("matricula", "Não informado");
        String cpf = prefs.getString("cpf", "Não informado");
        String tipo = prefs.getString("tipo", "Não informado");

        tvNome.setText("Nome: " + nome);
        tvMatricula.setText("Matrícula: " + matricula);
        tvCpf.setText("CPF: " + cpf);
        tvTipo.setText("Tipo de conta: " + tipo);

        Button btnTrocarSenha = findViewById(R.id.btnTrocarSenha);
        btnTrocarSenha.setOnClickListener(v -> {
            Intent intent = new Intent(InfosDeUsuario.this, TrocarSenhaActivity.class);
            startActivity(intent);
        });
    }
}
