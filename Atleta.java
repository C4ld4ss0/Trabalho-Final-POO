import java.util.Objects;

/**
 * Primeira especialização de Pessoa: um atleta, que pratica uma modalidade.
 * Herda nome, CPF e idade e acrescenta a modalidade.
 */
public class Atleta extends Pessoa {
    private String modalidade;

    /** super(...) chama o construtor de Pessoa, passando a idade mínima de atleta (10 anos). */
    public Atleta(String nome, String cpf, int idade, String modalidade) {
        super(nome, cpf, idade, Validador.IDADE_MIN_ATLETA);
        this.modalidade = Validador.validarTexto(modalidade, "modalidade");
    }

    public String getModalidade() {return modalidade;}

    /** Ver explicação em Pessoa.obterDadosFormatados(). */
    @Override
    public String obterDadosFormatados() {
        return String.format("Atleta: %s, CPF: %s, Modalidade: %s",
        getNome(), getCpfFormatado(), getModalidade());
    }

    /** Ver explicação em Pessoa.paraLinhaCsv(). Formato: Atleta;nome;cpf;idade;modalidade */
    @Override
    public String paraLinhaCsv() {
        return "Atleta;" + getNome() + ";" + getCpf() + ";" + getIdade() + ";" + getModalidade();
    }

    /** Ver explicação em Pessoa.copiar(). */
    @Override
    public Pessoa copiar() {
        return new Atleta(getNome(), getCpf(), getIdade(), getModalidade());
    }

    /**
     * Ver explicação em Pessoa.equals(). Primeiro reaproveita a comparação da Pessoa
     * (super.equals) e, se passar, compara também a modalidade.
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Atleta outro = (Atleta) obj;   // seguro: o super.equals já garantiu que é um Atleta
        return getModalidade().equalsIgnoreCase(outro.getModalidade());
    }

    /** Ver explicação em Pessoa.hashCode(). Soma ao cálculo da Pessoa o campo modalidade. */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getModalidade().toLowerCase());
    }
}
