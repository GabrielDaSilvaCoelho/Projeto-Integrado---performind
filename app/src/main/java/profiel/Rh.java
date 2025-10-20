package profiel;

import java.util.ArrayList;
import java.util.List;

public class Rh {

    private String nome;
    private String cpf;
    private String contato;

    private List<Colaborador> colaboradores;
    private List<Supervisor> supervisores;
    private List<Rh> rhCadastrados;

    public Rh(String nome, String cpf, String contato) {
        this.nome = nome;
        this.cpf = cpf;
        this.contato = contato;
        this.colaboradores = new ArrayList<>();
        this.supervisores = new ArrayList<>();
        this.rhCadastrados = new ArrayList<>();
    }

    /**
     * Método genérico de criação de usuário.
     * tipoUsuario pode ser: "RH", "Colaborador" ou "Supervisor".
     */
    public Object criarUsuario(String tipoUsuario, String nome, String matricula,
                               String cargo, String setorOuSupervisor,
                               String cpf, String contato) {

        switch (tipoUsuario.toUpperCase()) {
            case "RH":
                Rh novoRh = new Rh(nome, cpf, contato);
                rhCadastrados.add(novoRh);
                return novoRh;

            case "COLABORADOR":
                Colaborador colaborador = new Colaborador(nome, matricula, cargo, setorOuSupervisor, cpf, contato);
                colaboradores.add(colaborador);
                return colaborador;

            case "SUPERVISOR":
                Supervisor supervisor = new Supervisor(nome, matricula, cargo, setorOuSupervisor, cpf, contato);
                supervisores.add(supervisor);
                return supervisor;

            default:
                System.out.println("Tipo de usuário inválido!");
                return null;
        }
    }

    public List<Colaborador> getColaboradores() { return colaboradores; }
    public List<Supervisor> getSupervisores() { return supervisores; }
    public List<Rh> getRhCadastrados() { return rhCadastrados; }

    @Override
    public String toString() {
        return "Usuário RH{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", contato='" + contato + '\'' +
                ", totalColaboradores=" + colaboradores.size() +
                ", totalSupervisores=" + supervisores.size() +
                '}';
    }
}
