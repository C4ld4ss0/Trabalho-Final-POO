import java.util.Objects;

public class Atleta extends Pessoa {
    private String Modalidade;

    public Atleta(String nome, String cpf, int idade, String Modalidade) {
        super(nome, cpf, idade, Validador.IDADE_MIN_ATLETA);
        this.Modalidade = Validador.validarTexto(Modalidade, "modalidade");
    }

    public String getModalidade() {return Modalidade;}

    @Override
    public String obterDadosFormatados() {
        return String.format("Atleta: %s, CPF: %s, Modalidade: %s",
        getNome(), getCpfFormatado(), getModalidade());
    }

    @Override
    public String paraLinhaCsv() {
        return "Atleta;" + getNome() + ";" + getCpf() + ";" + getIdade() + ";" + getModalidade();
    }

    @Override
    public Pessoa copiar() {
        return new Atleta(getNome(), getCpf(), getIdade(), getModalidade());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Atleta outro = (Atleta) obj;
        return getModalidade().equalsIgnoreCase(outro.getModalidade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getModalidade().toLowerCase());
    }
}
