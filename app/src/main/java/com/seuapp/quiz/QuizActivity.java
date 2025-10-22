package com.seuapp.quiz;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.seu.pacote.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvProgress, tvQuestion, tvFeedback;
    private RadioGroup rgOptions;
    private RadioButton rb0, rb1, rb2, rb3, rb4;
    private MaterialButton btnConfirm, btnNext;
    private MaterialCardView cardRoot;

    private final List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private List<String> shuffledOptions;
    private int correctIndexInShuffled = -1;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        bindViews();
        loadQuestionsFromIntentOrSeed();
        loadQuestion(0);

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (!answered) btnConfirm.setEnabled(checkedId != -1);
        });

        btnConfirm.setOnClickListener(v -> {
            if (!answered) validateAnswer();
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex + 1 < questions.size()) {
                loadQuestion(++currentIndex);
            } else {
                Toast.makeText(this,
                        "Fim! Pontuação: " + score + "/" + questions.size(),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void bindViews() {
        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvFeedback = findViewById(R.id.tvFeedback);
        rgOptions  = findViewById(R.id.rgOptions);
        rb0 = findViewById(R.id.rb0);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnNext    = findViewById(R.id.btnNext);
        cardRoot   = findViewById(R.id.cardRoot);
    }

    private void loadQuestionsFromIntentOrSeed() {
        ArrayList<Question> recebidas = getIntent().getParcelableArrayListExtra("questions");
        if (recebidas != null && !recebidas.isEmpty()) {
            questions.clear();
            questions.addAll(recebidas);
        } else {
            seedSampleQuestions();
        }
    }

    private void seedSampleQuestions() {
        questions.add(new Question(
                "Qual expressão correta para a 2ª Lei de Newton?",
                Arrays.asList("F = m * a", "F = m / a", "F = a / m", "F = m + a", "F = m - a"),
                0
        ));
        questions.add(new Question(
                "Capital de Goiás?",
                Arrays.asList("Goiânia", "Anápolis", "Aparecida de Goiânia", "Trindade", "Catalão"),
                0
        ));
        questions.add(new Question(
                "Em SQL, qual comando cria uma tabela?",
                Arrays.asList("CREATE TABLE", "INSERT INTO", "SELECT", "UPDATE", "DROP DATABASE"),
                0
        ));
    }

    private void loadQuestion(int index) {
        answered = false;
        btnConfirm.setEnabled(false);
        btnNext.setEnabled(false);
        tvFeedback.setText("");
        tvFeedback.setVisibility(TextView.GONE);

        resetOptionStyles();

        Question q = questions.get(index);
        tvProgress.setText("Pergunta " + (index + 1) + "/" + questions.size());
        tvQuestion.setText(q.enunciado);

        Shuffled s = shuffleQuestion(q);
        shuffledOptions = s.alternativas;
        correctIndexInShuffled = s.idxCorretaNaLista;

        rb0.setText(shuffledOptions.get(0));
        rb1.setText(shuffledOptions.get(1));
        rb2.setText(shuffledOptions.get(2));
        rb3.setText(shuffledOptions.get(3));
        rb4.setText(shuffledOptions.get(4));

        rgOptions.clearCheck();
        setOptionsEnabled(true);

        btnNext.setText(index == questions.size() - 1 ? "Finalizar" : "Próxima");
    }

    private void validateAnswer() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) return;

        int chosenIndex = idToIndex(checkedId);
        boolean correct = chosenIndex == correctIndexInShuffled;

        markCorrectAndWrong(chosenIndex, correctIndexInShuffled);

        tvFeedback.setVisibility(TextView.VISIBLE);
        if (correct) {
            tvFeedback.setText("Correto! ✅");
            score++;
        } else {
            String corretaTxt = shuffledOptions.get(correctIndexInShuffled);
            tvFeedback.setText("Incorreto. Resposta correta: " + corretaTxt + " ❇️");
        }

        setOptionsEnabled(false);
        btnConfirm.setEnabled(false);
        btnNext.setEnabled(true);
        answered = true;
    }

    private void setOptionsEnabled(boolean enabled) {
        for (RadioButton rb : getAllRbs()) rb.setEnabled(enabled);
    }

    private List<RadioButton> getAllRbs() {
        return Arrays.asList(rb0, rb1, rb2, rb3, rb4);
    }

    private void resetOptionStyles() {
        @ColorInt int defaultColor = ContextCompat.getColor(this, android.R.color.primary_text_light);
        for (RadioButton rb : getAllRbs()) {
            rb.setTextColor(defaultColor);
            rb.setBackground(null);
        }
    }

    private void markCorrectAndWrong(int chosen, int correct) {
        @ColorInt int green = ContextCompat.getColor(this, android.R.color.holo_green_dark);
        @ColorInt int red   = ContextCompat.getColor(this, android.R.color.holo_red_dark);

        getRbByIndex(correct).setTextColor(green);
        if (chosen != correct) getRbByIndex(chosen).setTextColor(red);
    }

    private RadioButton getRbByIndex(int idx) {
        switch (idx) {
            case 0: return rb0;
            case 1: return rb1;
            case 2: return rb2;
            case 3: return rb3;
            case 4: return rb4;
            default: return rb0;
        }
    }

    private int idToIndex(int checkedId) {
        if (checkedId == R.id.rb0) return 0;
        if (checkedId == R.id.rb1) return 1;
        if (checkedId == R.id.rb2) return 2;
        if (checkedId == R.id.rb3) return 3;
        if (checkedId == R.id.rb4) return 4;
        return -1;
    }

    private Shuffled shuffleQuestion(Question q) {
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < 5; i++) idx.add(i);
        Collections.shuffle(idx);

        List<String> novas = new ArrayList<>(5);
        int novaCorreta = -1;
        for (int pos = 0; pos < 5; pos++) {
            int old = idx.get(pos);
            novas.add(q.alternativas.get(old));
            if (old == q.idxCorreta) novaCorreta = pos;
        }
        Shuffled s = new Shuffled();
        s.alternativas = novas;
        s.idxCorretaNaLista = novaCorreta;
        return s;
    }

    private static class Shuffled {
        List<String> alternativas;
        int idxCorretaNaLista;
    }
}
