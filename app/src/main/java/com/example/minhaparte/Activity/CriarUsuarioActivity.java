package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minhaparte.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;

public class CriarUsuarioActivity extends AppCompatActivity {
    private Spinner spTipo;
    private TextInputEditText etNome, etMatricula, etSenha, etCargo, etSetor, etSupervisor, etCpf, etContato;
    private TextInputLayout tilSetor, tilSupervisor;
    private Button btnSalvar, btnCancelar;

    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    enum TipoUsuario { RH, COLABORADOR, SUPERVISOR }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"RH", "Colaborador", "Supervisor"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        spTipo.setOnItemSelectedListener(new SimpleItemSelectedListener(this::atualizarCampos));
        btnCancelar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> {
            if (validar()) salvarUsuario();
        });

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

                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("nome", getText(etNome));
                jsonMap.put("matricula", getText(etMatricula));
                jsonMap.put("senha", sha256Hex(getText(etSenha)));
                jsonMap.put("tipo", tipo.name());
                jsonMap.put("cargo", getText(etCargo));
                jsonMap.put("setor", getText(etSetor));
                jsonMap.put("cpf", getText(etCpf));
                jsonMap.put("contato", getText(etContato));
                Gson gson = new Gson();
                String jsonString = gson.toJson(jsonMap);

                URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonString.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == 201) {
                        Toast.makeText(this, "Usuário salvo com sucesso!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao salvar usuário: HTTP " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });
                conn.disconnect();

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String sha256Hex(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e("HASH_ERROR", "Erro ao gerar hash", e);
            return null;
        }
    }
    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onChange;
        SimpleItemSelectedListener(Runnable onChange) { this.onChange = onChange; }
        @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { onChange.run(); }
        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
    }
}
