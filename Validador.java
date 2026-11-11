/**
 * Reúne todas as regras de validação dos dados em um só lugar.
 * Todos os métodos são static: são chamados direto pela classe (Validador.validarNome(...)),
 * sem precisar criar um objeto. Quando um dado é inválido, o método lança
 * IllegalArgumentException com a mensagem que aparece para o usuário.
 * Usado pelos construtores (Pessoa, Atleta, Instrutor) e pelo cadastro da TelaPrincipal.
 */
public final class Validador {

    // Limites usados nas validações (final = constante, não muda)
    public static final double SALARIO_MINIMO = 1621.00;
    public static final int IDADE_MIN_ATLETA = 10;
    public static final int IDADE_MIN_INSTRUTOR = 18;
    public static final int IDADE_MAX = 100;

    // Construtor privado: impede que alguém crie um "new Validador()", já que tudo é static.
    private Validador() {}

    /**
     * Nome: obrigatório, de 3 a 100 caracteres, só letras (com acento), espaço, hífen e apóstrofo.
     * Devolve o nome sem espaços sobrando.
     */
    public static String validarNome(String nome) {
        // null = não veio texto nenhum (ex.: Cancelar); isEmpty = veio texto vazio.
        // O null é testado primeiro porque chamar trim() em null daria erro.
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        // "\\s+" = um ou mais espaços seguidos; troca por um só ("Ana    Maria" -> "Ana Maria")
        String n = nome.trim().replaceAll("\\s+", " ");
        if (n.length() < 3 || n.length() > 100) {
            throw new IllegalArgumentException("O nome deve ter entre 3 e 100 caracteres.");
        }
        // matches confere se o texto inteiro segue o padrão: \\p{L} = qualquer letra (com acento),
        // seguida opcionalmente de grupos "espaço/hífen/apóstrofo + letras"
        if (!n.matches("\\p{L}+([ '\\-]\\p{L}+)*")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras, espaços, hífen ou apóstrofo (sem números).");
        }
        return n;
    }

    /**
     * CPF: aceita números, pontos, traço e espaços, mas precisa ter exatamente 11 números
     * e eles não podem ser todos iguais. Devolve só os números (ex.: "10000000001").
     */
    public static String validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {   // ver explicação em validarNome
            throw new IllegalArgumentException("O CPF não pode ser vazio.");
        }
        // [0-9.\\-\\s]+ = um ou mais caracteres, cada um sendo dígito, ponto, traço ou espaço
        if (!cpf.trim().matches("[0-9.\\-\\s]+")) {
            throw new IllegalArgumentException("O CPF deve conter apenas números. Pontos, traço e espaços são aceitos.");
        }
        // [^0-9] = tudo que NÃO é número; é trocado por "" (apagado), sobrando só os dígitos
        String digitos = cpf.replaceAll("[^0-9]", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("O CPF deve ter exatamente 11 dígitos (informado: " + digitos.length() + ").");
        }
        // chars() percorre os caracteres, distinct() tira os repetidos e count() conta os que sobraram.
        // Se sobrou só 1 caractere diferente, todos os dígitos são iguais (ex.: 111.111.111-11).
        if (digitos.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais.");
        }
        return digitos;
    }

    /**
     * Converte o texto digitado em número inteiro (o JOptionPane sempre devolve String).
     * [0-9]{1,3} = de 1 a 3 dígitos. Usado no cadastro (campo idade).
     */
    public static int lerIdade(String texto) {
        if (texto == null || !texto.trim().matches("[0-9]{1,3}")) {
            throw new IllegalArgumentException("A idade deve ser um número inteiro.");
        }
        return Integer.parseInt(texto.trim());   // conversão de texto para int (não é cast)
    }

    /**
     * Converte o texto digitado em salário. Aceita 3200, 3200.50, 3200,50, 3.200,50 e R$ 3.200,50.
     * Usado no cadastro de instrutor e no botão "Alterar Salário".
     */
    public static double lerSalario(String texto) {
        String msg = "O salário deve ser um número (ex.: 3200,50).";
        // ternário: se texto for null usa "", senão tira espaços das pontas, o "R$" e os espaços do meio
        String s = texto == null ? "" : texto.trim().replace("R$", "").replace(" ", "");
        if (s.isEmpty() || !s.matches("[0-9.,]+")) {   // só dígitos, ponto e vírgula
            throw new IllegalArgumentException(msg);
        }
        // O parseDouble só entende o formato americano (3200.50), então:
        if (s.contains(",")) {
            // tem vírgula -> a vírgula é o decimal e os pontos são milhar: "3.200,50" -> "3200.50"
            s = s.replace(".", "").replace(",", ".");
        } else if (s.matches("[0-9]{1,3}(\\.[0-9]{3})+")) {
            // sem vírgula, mas o ponto separa grupos de 3 -> é milhar: "3.200" -> "3200"
            s = s.replace(".", "");
        }
        // nos outros casos ("3200" ou "3200.50") o texto já está no formato certo
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(msg);
        }
    }

    /** Idade dentro do intervalo [min, max]. Usado no construtor de Pessoa e no cadastro. */
    public static int validarIdade(int idade, int min, int max) {
        if (idade < min || idade > max) {
            throw new IllegalArgumentException("A idade deve estar entre " + min + " e " + max + " anos.");
        }
        return idade;
    }

    /**
     * Modalidade (Atleta) e especialidade (Instrutor): obrigatório, de 3 a 50 caracteres, só letras.
     * O parâmetro "campo" só serve para montar a mensagem de erro.
     * Mesmas regras do validarNome (ver explicação lá), sem aceitar apóstrofo.
     */
    public static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + campo + " não pode ser vazio.");
        }
        String v = valor.trim().replaceAll("\\s+", " ");
        if (v.length() < 3 || v.length() > 50) {
            throw new IllegalArgumentException("O campo " + campo + " deve ter entre 3 e 50 caracteres.");
        }
        if (!v.matches("\\p{L}+([ \\-]\\p{L}+)*")) {
            throw new IllegalArgumentException("O campo " + campo + " deve conter apenas letras.");
        }
        return v;
    }

    /**
     * Salário precisa ser um número de verdade e pelo menos o salário mínimo.
     * NaN ("não é um número") e Infinity são valores especiais do double; aqui são recusados.
     * Usado no construtor do Instrutor, no setSalario e no cadastro.
     */
    public static double validarSalario(double salario) {
        if (Double.isNaN(salario) || Double.isInfinite(salario)) {
            throw new IllegalArgumentException("Salário inválido.");
        }
        if (salario < SALARIO_MINIMO) {
            throw new IllegalArgumentException(String.format("O salário deve ser no mínimo R$ %.2f.", SALARIO_MINIMO));
        }
        return salario;
    }
}
