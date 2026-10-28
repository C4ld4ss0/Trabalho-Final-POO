
public abstract class Pessoa {
    private String nome;
    private String cpf;
    private int idade;

    public Pessoa(String nome, String cpf, int idade){
        this.nome = nome;
        this.cpf = SimplificarCpf(cpf);
        this.idade = idade;
    }
//
    private String SimplificarCpf(String cpfBruto) {
        if (cpfBruto == null) {
            return "";
        }
        return cpfBruto.replaceAll("[^0-9]", "");
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
        return this.cpf; // Retorna o CPF sem formatação se não tiver 11 dígitos
    }

    public String getNome() {return nome;}

    public int getIdade() {return idade;}

    public abstract String obterDadosFormatados();
    
    public void exibirDados(){
        System.out.println(obterDadosFormatados());
    }
}