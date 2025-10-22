package profiel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.performind.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;




import java.util.ArrayList;
import java.util.List;

/** Activity para RH criar usuários: RH, Colaborador, Supervisor */
public class CriarUsuarioActivity extends AppCompatActivity {

    private Spinner spTipo;
    private TextInputEditText etNome, etMatricula, etCargo, etSetor, etSupervisor, etCpf, etContato;
    private TextInputLayout tilSetor, tilSupervisor;
    private Button btnSalvar, btnCancelar;

    enum TipoUsuario { RH, COLABORADOR, SUPERVISOR }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rh_creator);

        spTipo       = findViewById(R.id.spTipo);
        etNome       = findViewById(R.id.etNome);
        etMatricula  = findViewById(R.id.etMatricula);
        etCargo      = findViewById(R.id.etCargo);
        etSetor      = findViewById(R.id.etSetor);
        etSupervisor = findViewById(R.id.etSupervisor);
        etCpf        = findViewById(R.id.etCpf);
        etContato    = findViewById(R.id.etContato);
        tilSetor     = findViewById(R.id.tilSetor);
        tilSupervisor= findViewById(R.id.tilSupervisor);
        btnSalvar    = findViewById(R.id.btnSalvar);
        btnCancelar  = findViewById(R.id.btnCancelar);

        // Configura Spinner de tipo de usuário
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"RH", "Colaborador", "Supervisor"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        // Alterna visibilidade de campos conforme tipo
        spTipo.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> atualizarCampos()));

        btnCancelar.setOnClickListener(v -> finish());

        btnSalvar.setOnClickListener(v -> {
            if (!validar()) return;
            salvarUsuario();
        });

        // Atualiza estado inicial
        atualizarCampos();
    }

    private void atualizarCampos() {
        TipoUsuario tipo = getTipoSelecionado();
        // Setor é exigido para Colaborador e Supervisor; opcional/oculto para RH
        boolean exigeSetor = (tipo == TipoUsuario.COLABORADOR || tipo == TipoUsuario.SUPERVISOR);
        tilSetor.setVisibility(exigeSetor ? View.VISIBLE : View.GONE);

        // Supervisor (nome) apenas quando criando um Colaborador
        tilSupervisor.setVisibility(tipo == TipoUsuario.COLABORADOR ? View.VISIBLE : View.GONE);
    }

    private TipoUsuario getTipoSelecionado() {
        String s = (String) spTipo.getSelectedItem();
        if ("Colaborador".equalsIgnoreCase(s)) return TipoUsuario.COLABORADOR;
        if ("Supervisor".equalsIgnoreCase(s))  return TipoUsuario.SUPERVISOR;
        return TipoUsuario.RH;
    }

    private boolean validar() {
        List<String> erros = new ArrayList<>();

        String nome = getText(etNome);
        String matricula = getText(etMatricula);
        String cargo = getText(etCargo);
        String setor = getText(etSetor);
        String supervisor = getText(etSupervisor);
        String cpf = getText(etCpf);
        String contato = getText(etContato);

        TipoUsuario tipo = getTipoSelecionado();

        if (TextUtils.isEmpty(nome)) erros.add("Nome");
        if (TextUtils.isEmpty(matricula)) erros.add("Matrícula");
        if (TextUtils.isEmpty(cargo)) erros.add("Cargo");
        if (TextUtils.isEmpty(cpf)) erros.add("CPF");
        if (TextUtils.isEmpty(contato)) erros.add("Contato");

        // Regras específicas
        if (tipo == TipoUsuario.COLABORADOR || tipo == TipoUsuario.SUPERVISOR) {
            if (TextUtils.isEmpty(setor)) erros.add("Setor");
        }
        if (tipo == TipoUsuario.COLABORADOR) {
            if (TextUtils.isEmpty(supervisor)) erros.add("Supervisor");
        }

        if (!erros.isEmpty()) {
            Toast.makeText(this,
                    "Preencha: " + TextUtils.join(", ", erros),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void salvarUsuario() {
        TipoUsuario tipo = getTipoSelecionado();

        String nome = getText(etNome);
        String matricula = getText(etMatricula);
        String cargo = getText(etCargo);
        String setor = getText(etSetor);
        String supervisorNome = getText(etSupervisor);
        String cpf = getText(etCpf);
        String contato = getText(etContato);

        switch (tipo) {
            case RH:
                RH rh = new RH(nome, matricula, cargo, cpf, contato);
                UsersRepository.get().addRH(rh);
                break;

            case COLABORADOR:
                Colaborador col = new Colaborador(nome, matricula, cargo, setor, supervisorNome, cpf, contato);
                UsersRepository.get().addColaborador(col);
                // também associa ao supervisor/setor caso exista supervisor cadastrado
                UsersRepository.get().vincularColaboradorAoSupervisor(col);
                break;

            case SUPERVISOR:
                Supervisor sup = new Supervisor(nome, matricula, cargo, setor, cpf, contato);
                UsersRepository.get().addSupervisor(sup);
                break;
        }

        Toast.makeText(this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Listener simples para Spinner sem boilerplate */
    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onChange;
        SimpleItemSelectedListener(Runnable onChange) { this.onChange = onChange; }
        @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { onChange.run(); }
        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
    }

    // ====== MODELOS (simples) ======
    public static class RH {
        public String nome, matricula, cargo, cpf, contato;
        public RH(String nome, String matricula, String cargo, String cpf, String contato) {
            this.nome = nome; this.matricula = matricula; this.cargo = cargo; this.cpf = cpf; this.contato = contato;
        }
    }

    public static class Colaborador {
        public String nome, matricula, cargo, setor, supervisorNome, cpf, contato;
        public Colaborador(String nome, String matricula, String cargo, String setor, String supervisorNome, String cpf, String contato) {
            this.nome = nome; this.matricula = matricula; this.cargo = cargo;
            this.setor = setor; this.supervisorNome = supervisorNome; this.cpf = cpf; this.contato = contato;
        }
    }

    public static class Supervisor {
        public String nome, matricula, cargo, setorResponsavel, cpf, contato;
        public final List<Colaborador> colaboradoresDoSetor = new ArrayList<>();
        public Supervisor(String nome, String matricula, String cargo, String setorResponsavel, String cpf, String contato) {
            this.nome = nome; this.matricula = matricula; this.cargo = cargo;
            this.setorResponsavel = setorResponsavel; this.cpf = cpf; this.contato = contato;
        }
    }

    // ====== REPOSITÓRIO EM MEMÓRIA ======
    public static class UsersRepository {
        private static UsersRepository INSTANCE;
        public static UsersRepository get() {
            if (INSTANCE == null) INSTANCE = new UsersRepository();
            return INSTANCE;
        }
        private final List<RH> rhs = new ArrayList<>();
        private final List<Colaborador> colaboradores = new ArrayList<>();
        private final List<Supervisor> supervisores = new ArrayList<>();

        public void addRH(RH rh) { rhs.add(rh); }
        public void addColaborador(Colaborador c) { colaboradores.add(c); }
        public void addSupervisor(Supervisor s) { supervisores.add(s); }

        /** Vincula colaborador ao supervisor cujo nome e setor coincidam */
        public void vincularColaboradorAoSupervisor(Colaborador c) {
            for (Supervisor s : supervisores) {
                if (s.setorResponsavel.equalsIgnoreCase(c.setor)
                        && s.nome.equalsIgnoreCase(c.supervisorNome)) {
                    s.colaboradoresDoSetor.add(c);
                    break;
                }
            }
        }
    }
}
