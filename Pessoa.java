import java.util.Objects;

/**
 * Classe base (mãe) da hierarquia: guarda o que todo membro da equipe tem em comum.
 * É abstrata, ou seja, não existe "uma Pessoa" sozinha: só Atleta ou Instrutor.
 * Os métodos abstratos obrigam cada subclasse a implementar a sua versão (polimorfismo).
 */
public abstract class Pessoa {
    private String nome;
    private String cpf;   // guardado só com os 11 números, ex.: "10000000001"
    private int idade;

    /**
     * Construtor chamado pelas subclasses através de super(...).
     * Cada dado passa pelo Validador: se algo estiver errado, é lançada uma
     * IllegalArgumentException e o objeto nem chega a ser criado.
     * idadeMinima vem da subclasse (atleta: 10 anos, instrutor: 18 anos).
     */
    public Pessoa(String nome, String cpf, int idade, int idadeMinima) {
        this.nome = Validador.validarNome(nome);
        this.cpf = Validador.validarCpf(cpf);
        this.idade = Validador.validarIdade(idade, idadeMinima, Validador.IDADE_MAX);
    }

    /** CPF só com números. Usado para comparar, buscar e gravar no arquivo (Equipe e paraLinhaCsv). */
    public String getCpf() {return cpf;}

    /**
     * Devolve o CPF com pontos e traço (ex.: "100.000.000-01"), só para exibir na tela.
     * Usado nas tabelas da TelaPrincipal e no obterDadosFormatados das subclasses.
     * substring(inicio, fim) recorta o texto: (0,3) = 3 primeiros números, e assim por diante.
     */
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

    /**
     * Texto com os dados da pessoa em uma linha, ex.: "Atleta: Ana, CPF: ..., Modalidade: Vôlei".
     * Abstrato: cada subclasse monta o seu. Usado na mensagem do botão "Duplicar Pessoa".
     */
    public abstract String obterDadosFormatados();

    /**
     * Linha que será gravada no arquivo equipe.csv, no mesmo formato que o importarCSV lê.
     * Abstrato: o Atleta grava 5 campos e o Instrutor 6 (tem salário). Usado em Equipe.salvarCSV.
     */
    public abstract String paraLinhaCsv();

    /**
     * Cria um objeto NOVO com os mesmos dados deste. Abstrato porque só a subclasse sabe
     * qual construtor chamar (new Atleta ou new Instrutor). Usado em Equipe.duplicarMembro.
     */
    public abstract Pessoa copiar();

    /**
     * Define quando duas pessoas são "iguais": mesmo tipo + mesmo nome, CPF e idade.
     * Sem isso, o Java só considera iguais se forem o MESMO objeto na memória,
     * e uma cópia nunca seria igual ao original.
     * Usado em Equipe.encontrarDuplicadas (Collections.frequency) e Equipe.removerMembro (List.remove).
     * As subclasses completam a comparação com os seus próprios campos.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                                       // é o mesmo objeto
        if (obj == null || getClass() != obj.getClass()) return false;      // nulo ou tipo diferente (Atleta x Instrutor)
        Pessoa outra = (Pessoa) obj;
        return idade == outra.idade
            && cpf.equals(outra.cpf)
            && nome.equalsIgnoreCase(outra.nome);                           // ignora maiúsculas/minúsculas
    }

    /**
     * Regra do Java: se dois objetos são iguais no equals, precisam ter o mesmo hashCode.
     * Por isso o cálculo usa exatamente os mesmos campos do equals (nome em minúsculo,
     * porque o equals ignora maiúsculas). Objects.hash junta os campos em um único número.
     * Nosso código usa List, que não chama hashCode; ele existe para o equals ficar correto
     * caso a Pessoa seja usada em HashSet ou HashMap.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase(), cpf, idade);
    }
}
