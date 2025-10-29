package com.example.minhaparte;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CriarUsuarioActivity extends AppCompatActivity {

    private Spinner spTipo;
    private TextInputEditText etNome, etMatricula, etSenha, etCargo, etSetor, etSupervisor, etCpf, etContato;
    private TextInputLayout tilSetor, tilSupervisor;
    private Button btnSalvar, btnCancelar, btnAbrirPlayer;

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    enum TipoUsuario { RH, COLABORADOR, SUPERVISOR }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);

        spTipo        = findViewById(R.id.spTipo);
        etNome        = findViewById(R.id.etNome);
        etMatricula   = findViewById(R.id.etMatricula);
        etSenha       = findViewById(R.id.etSenha);
        etCargo       = findViewById(R.id.etCargo);
        etSetor       = findViewById(R.id.etSetor);
        etSupervisor  = findViewById(R.id.etSupervisor);
        etCpf         = findViewById(R.id.etCpf);
        etContato     = findViewById(R.id.etContato);
        tilSetor      = findViewById(R.id.tilSetor);
        tilSupervisor = findViewById(R.id.tilSupervisor);
        btnSalvar     = findViewById(R.id.btnSalvar);
        btnCancelar   = findViewById(R.id.btnCancelar);
        btnAbrirPlayer= findViewById(R.id.btnAbrirPlayer);

        // Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"RH", "Colaborador", "Supervisor"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        spTipo.setOnItemSelectedListener(new SimpleItemSelectedListener(this::atualizarCampos));
        btnCancelar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> { if (validar()) salvarUsuario(); });

        // Novo botão: abre o player
        btnAbrirPlayer.setOnClickListener(v ->
                startActivity(new Intent(CriarUsuarioActivity.this, PlayerActivity.class))
        );

        atualizarCampos();
    }

    private void atualizarCampos() {
        TipoUsuario tipo = getTipoSelecionado();
        tilSetor.setVisibility(tipo == TipoUsuario.COLABORADOR || tipo == TipoUsuario.SUPERVISOR ? View.VISIBLE : View.GONE);
        tilSupervisor.setVisibility(tipo == TipoUsuario.COLABORADOR ? View.VISIBLE : View.GONE);
    }

    private TipoUsuario getTipoSelecionado() {
        String s = (String) spTipo.getSelectedItem();
        if ("Colaborador".equalsIgnoreCase(s)) return TipoUsuario.COLABORADOR;
        if ("Supervisor".equalsIgnoreCase(s)) return TipoUsuario.SUPERVISOR;
        return TipoUsuario.RH;
    }

    private boolean validar() {
        TipoUsuario tipo = getTipoSelecionado();
        String nome = getText(etNome);
        String matricula = getText(etMatricula);
        String senha = getText(etSenha);
        String cargo = getText(etCargo);
        String setor = getText(etSetor);
        String supervisor = getText(etSupervisor);
        String cpf = getText(etCpf);
        String contato = getText(etContato);

        StringBuilder erros = new StringBuilder();
        if (TextUtils.isEmpty(nome)) erros.append("Nome, ");
        if (TextUtils.isEmpty(matricula)) erros.append("Matrícula, ");
        if (TextUtils.isEmpty(senha)) erros.append("Senha, ");
        if (TextUtils.isEmpty(cargo)) erros.append("Cargo, ");
        if (TextUtils.isEmpty(cpf)) erros.append("CPF, ");
        if (TextUtils.isEmpty(contato)) erros.append("Contato, ");
        if ((tipo == TipoUsuario.COLABORADOR || tipo == TipoUsuario.SUPERVISOR) && TextUtils.isEmpty(setor))
            erros.append("Setor, ");
        if (tipo == TipoUsuario.COLABORADOR && TextUtils.isEmpty(supervisor))
            erros.append("Supervisor, ");

        if (erros.length() > 0) {
            Toast.makeText(this, "Preencha: " + erros.substring(0, erros.length() - 2), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private static String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void salvarUsuario() {
        new Thread(() -> {
            try {
                TipoUsuario tipo = getTipoSelecionado();
                JSONObject json = new JSONObject();
                json.put("nome", getText(etNome));
                json.put("matricula", getText(etMatricula));
                json.put("senha", getText(etSenha));         // considere hashear/armazenar com segurança no backend
                json.put("tipo", tipo.name());
                json.put("cargo", getText(etCargo));
                json.put("setor", getText(etSetor));
                json.put("supervisor_nome", getText(etSupervisor));
                json.put("cpf", getText(etCpf));
                json.put("contato", getText(etContato));

                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                // PostgREST aceita array JSON para inserir múltiplas linhas; aqui enviamos um único objeto dentro de array
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(("[" + json.toString() + "]").getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 201 || responseCode == 200) {
                        Toast.makeText(this, "✅ Usuário salvo com sucesso!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "❌ Erro ao salvar usuário: HTTP " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Listener Spinner simplificado
    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onChange;
        SimpleItemSelectedListener(Runnable onChange) { this.onChange = onChange; }
        @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { onChange.run(); }
        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
    }
}
