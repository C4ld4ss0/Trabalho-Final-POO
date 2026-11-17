public abstract class Pessoa {
    private String nome;
    private String cpf;
    private int idade;

    public Pessoa(String nome, String cpf, int idade, int idadeMinima) {
        this.nome = Validador.validarNome(nome);
        this.cpf = Validador.validarCpf(cpf);
        this.idade = Validador.validarIdade(idade, idadeMinima, Validador.IDADE_MAX);
    }

    public String getCpf() {return cpf;}

    public String getCpfFormatado() {
        String cpfNumeros = getCpf();
        if (cpfNumeros != null && cpfNumeros.length() == 11) {
            return String.format("%s.%s.%s-%s",
                cpfNumeros.substring(0, 3),
                cpfNumeros.substring(3, 6),
                cpfNumeros.substring(6, 9),
                cpfNumeros.substring(9, 11));
        }
        return this.cpf;
    }

    public String getNome() {return nome;}

    public int getIdade() {return idade;}

    public abstract String obterDadosFormatados();

    public void exibirDados() {
        System.out.println(obterDadosFormatados());
    }
}
