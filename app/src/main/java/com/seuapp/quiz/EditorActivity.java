package com.seuapp.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.performind.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    private static final int LIMITE = 10;

    private TextInputEditText etEnunciado, etCorreta, etErrada1, etErrada2, etErrada3, etErrada4;
    private MaterialButton btnAdd, btnStartQuiz;
    private TextView tvCount;
    private RecyclerView rvPerguntas;

    private final ArrayList<Question> perguntas = new ArrayList<>();
    private PerguntaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        bindViews();
        setupList();

        btnAdd.setOnClickListener(v -> onAddPergunta());
        btnStartQuiz.setOnClickListener(v -> onStartQuiz());
    }

    private void bindViews() {
        etEnunciado = findViewById(R.id.etEnunciado);
        etCorreta   = findViewById(R.id.etCorreta);
        etErrada1   = findViewById(R.id.etErrada1);
        etErrada2   = findViewById(R.id.etErrada2);
        etErrada3   = findViewById(R.id.etErrada3);
        etErrada4   = findViewById(R.id.etErrada4);

        btnAdd      = findViewById(R.id.btnAdd);
        btnStartQuiz= findViewById(R.id.btnStartQuiz);
        tvCount     = findViewById(R.id.tvCount);
        rvPerguntas = findViewById(R.id.rvPerguntas);
    }

    private void setupList() {
        adapter = new PerguntaAdapter(perguntas);
        rvPerguntas.setLayoutManager(new LinearLayoutManager(this));
        rvPerguntas.setAdapter(adapter);
        updateCounter();
    }

    private void updateCounter() {
        tvCount.setText(perguntas.size() + "/" + LIMITE + " perguntas");
        btnStartQuiz.setEnabled(!perguntas.isEmpty());
        btnAdd.setEnabled(perguntas.size() < LIMITE);
    }

    private void onAddPergunta() {
        if (perguntas.size() >= LIMITE) {
            Toast.makeText(this, "Limite de 10 perguntas atingido", Toast.LENGTH_SHORT).show();
            return;
        }

        String en = textOf(etEnunciado);
        String c  = textOf(etCorreta);
        String e1 = textOf(etErrada1);
        String e2 = textOf(etErrada2);
        String e3 = textOf(etErrada3);
        String e4 = textOf(etErrada4);

        if (TextUtils.isEmpty(en) || TextUtils.isEmpty(c)
                || TextUtils.isEmpty(e1) || TextUtils.isEmpty(e2)
                || TextUtils.isEmpty(e3) || TextUtils.isEmpty(e4)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasDuplicates(Arrays.asList(c, e1, e2, e3, e4))) {
            Toast.makeText(this, "As alternativas devem ser diferentes entre si", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> alternativas = new ArrayList<>(5);
        alternativas.add(c);   // índice 0 = correta
        alternativas.add(e1);
        alternativas.add(e2);
        alternativas.add(e3);
        alternativas.add(e4);

        Question q = new Question(en, alternativas, 0);
        perguntas.add(q);
        adapter.notifyItemInserted(perguntas.size() - 1);
        updateCounter();

        clearInputs();
        etEnunciado.requestFocus();
        etEnunciado.onEditorAction(EditorInfo.IME_ACTION_NEXT);
    }

    private boolean hasDuplicates(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String a = list.get(i).trim();
            for (int j = i + 1; j < list.size(); j++) {
                if (a.equalsIgnoreCase(list.get(j).trim())) return true;
            }
        }
        return false;
    }

    private void clearInputs() {
        etEnunciado.setText("");
        etCorreta.setText("");
        etErrada1.setText("");
        etErrada2.setText("");
        etErrada3.setText("");
        etErrada4.setText("");
    }

    private String textOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void onStartQuiz() {
        Intent it = new Intent(this, QuizActivity.class);
        it.putParcelableArrayListExtra("questions", perguntas);
        startActivity(it);
    }

    // ===== RecyclerView Adapter =====
    private static class PerguntaAdapter extends RecyclerView.Adapter<PerguntaVH> {
        private final List<Question> data;
        PerguntaAdapter(List<Question> data) { this.data = data; }

        @NonNull
        @Override
        public PerguntaVH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_created_question, parent, false);
            return new PerguntaVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PerguntaVH h, int position) {
            Question q = data.get(position);
            h.tvTitulo.setText(q.enunciado);
            String detalhe = "Correta: " + q.alternativas.get(q.idxCorreta) + " • Opções: " + q.alternativas.size();
            h.tvDetalhe.setText(detalhe);
        }

        @Override
        public int getItemCount() { return data.size(); }
    }

    private static class PerguntaVH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalhe;
        PerguntaVH(@NonNull android.view.View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDetalhe = itemView.findViewById(R.id.tvDetalhe);
        }
    }
}
