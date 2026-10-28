
public class Atleta extends Pessoa {
    private String Modalidade;
//
    public Atleta(String nome, String cpf, int idade, String Modalidade) {
        super(nome, cpf, idade);
        this.Modalidade = Modalidade;
    }

    public String getModalidade() {return Modalidade;}

    @Override
    public String obterDadosFormatados() {
        return String.format("Atleta: %s, CPF: %s, Modalidade: %s", 
        getNome(), getCpfFormatado(), getModalidade());
    }
}