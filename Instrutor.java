import java.util.Objects;

/**
 * Segunda especialização de Pessoa: um instrutor, que tem especialidade e salário.
 * O salário é o dado numérico usado na soma e na média das estatísticas.
 */
public class Instrutor extends Pessoa {
    private String especialidade;
    private double salario;

    /** Igual ao construtor do Atleta, mas com idade mínima de 18 anos e validação do salário. */
    public Instrutor(String nome, String cpf, int idade, String especialidade, double salario) {
        super(nome, cpf, idade, Validador.IDADE_MIN_INSTRUTOR);
        this.especialidade = Validador.validarTexto(especialidade, "especialidade");
        this.salario = Validador.validarSalario(salario);
    }

    public String getEspecialidade() {return especialidade;}

    public double getSalario() {return salario;}

    /**
     * Altera o salário. Regra do grupo: o novo valor precisa ser válido e MAIOR que o atual.
     * Usado pelo botão "Alterar Salário" da aba Financeiro (TelaPrincipal.alterarSalario).
     */
    public void setSalario(double novoSalario) {
        Validador.validarSalario(novoSalario);
        if (novoSalario <= this.salario) {
            throw new IllegalArgumentException("O novo salário deve ser maior que o salário atual.");
        }
        this.salario = novoSalario;
    }

    /** Ver explicação em Pessoa.obterDadosFormatados(). */
    @Override
    public String obterDadosFormatados() {
        return String.format("Instrutor: %s, CPF: %s, Especialidade: %s, Salário: %.2f",
        getNome(), getCpfFormatado(), getEspecialidade(), getSalario());
    }

    /** Ver explicação em Pessoa.paraLinhaCsv(). Formato: Instrutor;nome;cpf;idade;especialidade;salario */
    @Override
    public String paraLinhaCsv() {
        return "Instrutor;" + getNome() + ";" + getCpf() + ";" + getIdade() + ";" + getEspecialidade() + ";" + getSalario();
    }

    /** Ver explicação em Pessoa.copiar(). */
    @Override
    public Pessoa copiar() {
        return new Instrutor(getNome(), getCpf(), getIdade(), getEspecialidade(), getSalario());
    }

    /**
     * Ver explicação em Atleta.equals(). Aqui compara especialidade e salário.
     * Double.compare devolve 0 quando os dois números são iguais.
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Instrutor outro = (Instrutor) obj;
        return getEspecialidade().equalsIgnoreCase(outro.getEspecialidade())
            && Double.compare(getSalario(), outro.getSalario()) == 0;
    }

    /** Ver explicação em Pessoa.hashCode(). Soma ao cálculo da Pessoa a especialidade e o salário. */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEspecialidade().toLowerCase(), getSalario());
    }
}
