public final class Validador {

    public static final double SALARIO_MINIMO = 1621.00;
    public static final int IDADE_MIN_ATLETA = 10;
    public static final int IDADE_MIN_INSTRUTOR = 18;
    public static final int IDADE_MAX = 100;

    private Validador() {}

    /** Nome: só letras (com acento), espaço, hífen e apóstrofo. Retorna o nome "limpo". */
    public static String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        String n = nome.trim().replaceAll("\\s+", " ");
        if (n.length() < 3 || n.length() > 100) {
            throw new IllegalArgumentException("O nome deve ter entre 3 e 100 caracteres.");
        }
        if (!n.matches("\\p{L}+([ '\\-]\\p{L}+)*")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras, espaços, hífen ou apóstrofo (sem números).");
        }
        return n;
    }

    /** CPF: aceita apenas dígitos, '.' e '-'; precisa ter exatamente 11 dígitos. Retorna só os números. */
    public static String validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser vazio.");
        }
        if (!cpf.trim().matches("[0-9.\\-\\s]+")) {
            throw new IllegalArgumentException("O CPF deve conter apenas números. Pontos, traço e espaços são aceitos.");
        }
        String digitos = cpf.replaceAll("[^0-9]", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("O CPF deve ter exatamente 11 dígitos (informado: " + digitos.length() + ").");
        }
        if (digitos.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais.");
        }
        return digitos;
    }

    /** Converte o texto digitado em idade (inteiro). */
    public static int lerIdade(String texto) {
        if (texto == null || !texto.trim().matches("[0-9]{1,3}")) {
            throw new IllegalArgumentException("A idade deve ser um número inteiro.");
        }
        return Integer.parseInt(texto.trim());
    }

    /** Converte o texto digitado em salário. Aceita 3200, 3200.50, 3200,50, 3.200,50 e R$ 3.200,50. */
    public static double lerSalario(String texto) {
        String msg = "O salário deve ser um número (ex.: 3200,50).";
        String s = texto == null ? "" : texto.trim().replace("R$", "").replace(" ", "");
        if (s.isEmpty() || !s.matches("[0-9.,]+")) {
            throw new IllegalArgumentException(msg);
        }
        if (s.contains(",")) {
            s = s.replace(".", "").replace(",", ".");
        } else if (s.matches("[0-9]{1,3}(\\.[0-9]{3})+")) {
            s = s.replace(".", "");
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static int validarIdade(int idade, int min, int max) {
        if (idade < min || idade > max) {
            throw new IllegalArgumentException("A idade deve estar entre " + min + " e " + max + " anos.");
        }
        return idade;
    }

    /** Modalidade / especialidade: obrigatória, só letras, espaços e hífen. */
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

    /** Salário: número finito (bloqueia NaN/Infinity) e >= salário mínimo. */
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
