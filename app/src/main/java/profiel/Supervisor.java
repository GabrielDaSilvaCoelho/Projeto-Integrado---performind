package profiel;

import java.util.ArrayList;
import java.util.List;

public class Supervisor {
    private String nome;
    private String matricula;
    private String cargo;
    private String setorResponsavel;
    private String cpf;
    private String contato;
    private List<Colaborador> equipe;

    public Supervisor(String nome, String matricula, String cargo, String setorResponsavel, String cpf, String contato) {
        this.nome = nome;
        this.matricula = matricula;
        this.cargo = cargo;
        this.setorResponsavel = setorResponsavel;
        this.cpf = cpf;
        this.contato = contato;
        this.equipe = new ArrayList<>();
    }

    public void adicionarColaborador(Colaborador colaborador) {
        equipe.add(colaborador);
    }

    public List<Colaborador> getEquipe() {
        return equipe;
    }

    public String getNome() { return nome; }
    public String getMatricula() { return matricula; }
    public String getCargo() { return cargo; }
    public String getSetorResponsavel() { return setorResponsavel; }
    public String getCpf() { return cpf; }
    public String getContato() { return contato; }

    @Override
    public String toString() {
        return "Supervisor{" +
                "nome='" + nome + '\'' +
                ", matrícula='" + matricula + '\'' +
                ", cargo='" + cargo + '\'' +
                ", setorResponsável='" + setorResponsavel + '\'' +
                ", cpf='" + cpf + '\'' +
                ", contato='" + contato + '\'' +
                ", equipe=" + equipe.size() +
                '}';
    }
}
