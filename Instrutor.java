import java.util.Objects;

public class Instrutor extends Pessoa {
    private String Especialidade;
    private double salario;

    public Instrutor(String nome, String cpf, int idade, String Especialidade, double salario) {
        super(nome, cpf, idade, Validador.IDADE_MIN_INSTRUTOR);
        this.Especialidade = Validador.validarTexto(Especialidade, "especialidade");
        this.salario = Validador.validarSalario(salario);
    }

    public String getEspecialidade() {return Especialidade;}

    public double getSalario() {return salario;}

    public void setSalario(double novoSalario) {
        Validador.validarSalario(novoSalario);
        if (novoSalario <= this.salario) {
            throw new IllegalArgumentException("O novo salário deve ser maior que o salário atual.");
        }
        this.salario = novoSalario;
    }

    @Override
    public String obterDadosFormatados() {
        return String.format("Instrutor: %s, CPF: %s, Especialidade: %s, Salário: %.2f",
        getNome(), getCpfFormatado(), getEspecialidade(), getSalario());
    }

    @Override
    public Pessoa copiar() {
        return new Instrutor(getNome(), getCpf(), getIdade(), getEspecialidade(), getSalario());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Instrutor outro = (Instrutor) obj;
        return getEspecialidade().equalsIgnoreCase(outro.getEspecialidade())
            && Double.compare(getSalario(), outro.getSalario()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEspecialidade().toLowerCase(), getSalario());
    }
}
