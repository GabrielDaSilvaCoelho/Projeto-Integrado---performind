package com.example.minhaparte.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.Adapter.DesempenhoColaboradorAdapter;
import com.example.minhaparte.Model.UsuarioModel;
import com.example.minhaparte.R;
import com.example.minhaparte.RetrofitClient;
import com.example.minhaparte.Service.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaDesempenhoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    DesempenhoColaboradorAdapter desempenhoAdapter;
    UsuarioService api;

    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";
    private final String AUTH = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_desempenho_colaboradores);

        recyclerView = findViewById(R.id.recyclerDesempenhoColab);
        progressBar = findViewById(R.id.progressBarDesempenhoColab);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.blue_500));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getClient().create(UsuarioService.class);

        carregarColaboradores();
    }

    private void carregarColaboradores() {
        progressBar.setVisibility(View.VISIBLE);

        api.getUsuarios(API_KEY, AUTH).enqueue(new Callback<List<UsuarioModel>>() {
            @Override
            public void onResponse(Call<List<UsuarioModel>> call, Response<List<UsuarioModel>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<UsuarioModel> lista = response.body();
                    if (lista == null) lista = new ArrayList<>();

                    // filtra apenas tipo "Colaborador"
                    List<UsuarioModel> colaboradores = new ArrayList<>();
                    for (UsuarioModel u : lista) {
                        if (u.tipo != null && u.tipo.equalsIgnoreCase("Colaborador")) {
                            colaboradores.add(u);
                        }
                    }

                    desempenhoAdapter = new DesempenhoColaboradorAdapter(
                            ListaDesempenhoActivity.this,
                            colaboradores
                    );
                    recyclerView.setAdapter(desempenhoAdapter);

                } else {
                    Toast.makeText(ListaDesempenhoActivity.this,
                            "Erro ao carregar usuários", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListaDesempenhoActivity.this,
                        "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
