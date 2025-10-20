package profiel;

public class Colaborador {
    private String nome;
    private String matricula;
    private String cargo;
    private String setorOuSupervisor;
    private String cpf;
    private String contato;

    public Colaborador(String nome, String matricula, String cargo, String setorOuSupervisor, String cpf, String contato) {
        this.nome = nome;
        this.matricula = matricula;
        this.cargo = cargo;
        this.setorOuSupervisor = setorOuSupervisor;
        this.cpf = cpf;
        this.contato = contato;
    }

    public String getNome() { return nome; }
    public String getMatricula() { return matricula; }
    public String getCargo() { return cargo; }
    public String getSetorOuSupervisor() { return setorOuSupervisor; }
    public String getCpf() { return cpf; }
    public String getContato() { return contato; }

    @Override
    public String toString() {
        return "Colaborador{" +
                "nome='" + nome + '\'' +
                ", matrícula='" + matricula + '\'' +
                ", cargo='" + cargo + '\'' +
                ", setor/supervisor='" + setorOuSupervisor + '\'' +
                ", cpf='" + cpf + '\'' +
                ", contato='" + contato + '\'' +
                '}';
    }
}
