public class Instrutor extends Pessoa {
    private String Especialidade;
    private double salario;

    public Instrutor(String nome, String cpf, int idade, String Especialidade, double salario) {
        super(nome, cpf, idade);
        this.Especialidade = Especialidade;
        this.salario = salario;
    }
//
    public String getEspecialidade() {return Especialidade;}

    public double getSalario() {return salario;}

    public void setSalario(double novosalario) {
            double salarioMin = 1621.00; 
            if (novosalario < salarioMin) {
            System.out.println("Salário inválido. O salário deve ser no mínimo R$ " + String.format("%.2f", salarioMin));
        } else if (novosalario <= this.salario) {
            System.out.println("Salário inválido. O novo salário deve ser maior que o salário atual.");
        } else {
            this.salario = novosalario;
            System.out.println("Salário atualizado com sucesso para R$ " + String.format("%.2f", novosalario));
        }
    }

    @Override
    public String obterDadosFormatados() {
        return String.format("Instrutor: %s, CPF: %s, Especialidade: %s, Salário: %.2f", 
        getNome(), getCpfFormatado(), getEspecialidade(), getSalario());
    }
}