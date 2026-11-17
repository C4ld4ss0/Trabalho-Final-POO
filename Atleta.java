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
}
