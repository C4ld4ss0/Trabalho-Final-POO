import java.util.Objects;

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

    /** Cria um novo objeto com os mesmos dados (cada subclasse sabe se copiar). */
    public abstract Pessoa copiar();

    /** Duas pessoas são iguais se forem do mesmo tipo e tiverem nome, CPF e idade iguais. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pessoa outra = (Pessoa) obj;
        return idade == outra.idade
            && cpf.equals(outra.cpf)
            && nome.equalsIgnoreCase(outra.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase(), cpf, idade);
    }

    public void exibirDados() {
        System.out.println(obterDadosFormatados());
    }
}
