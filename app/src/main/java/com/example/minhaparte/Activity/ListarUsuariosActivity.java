package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.Model.UsuarioModel;
import com.example.minhaparte.R;
import com.example.minhaparte.RetrofitClient;
import com.example.minhaparte.Service.UsuarioService;
import com.example.minhaparte.Adapter.UsuarioAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListarUsuariosActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    UsuarioAdapter usuarioAdapter;
    UsuarioService api;

    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
    private final String AUTH = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_usuarios);
        recyclerView = findViewById(R.id.recyclerUsuarios);
        progressBar = findViewById(R.id.progressBarUsuarios);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        api = RetrofitClient.getClient().create(UsuarioService.class);
        carregarUsuarios();
    }
    private void carregarUsuarios() {
        progressBar.setVisibility(View.VISIBLE);

        api.getUsuarios(API_KEY, AUTH).enqueue(new Callback<List<UsuarioModel>>() {
            @Override
            public void onResponse(Call<List<UsuarioModel>> call, Response<List<UsuarioModel>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<UsuarioModel> lista = response.body();
                    usuarioAdapter = new UsuarioAdapter(lista, usuario -> confirmarExclusao(usuario.id));
                    recyclerView.setAdapter(usuarioAdapter);
                } else {
                    Toast.makeText(ListarUsuariosActivity.this, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<UsuarioModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListarUsuariosActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmarExclusao(long id) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir usuário")
                .setMessage("Tem certeza que deseja excluir este usuário?")
                .setPositiveButton("Sim", (dialog, which) -> excluirUsuario(id))
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void excluirUsuario(long id) {
        String filtro = "eq." + id;

        api.deleteUsuario(filtro, API_KEY, AUTH)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ListarUsuariosActivity.this, "Usuário excluído!", Toast.LENGTH_SHORT).show();
                            carregarUsuarios(); // atualiza a lista
                        } else {
                            Toast.makeText(ListarUsuariosActivity.this, "Falha ao excluir! Código: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ListarUsuariosActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
