import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Equipe {
    private List<Pessoa> membros;
    private List<Registroduplicata> duplicatasEncontradas;
//
    public Equipe() {
        this.membros = new ArrayList<>();
        this.duplicatasEncontradas = new ArrayList<>();
    }

    public List<Pessoa> getMembros() {
        return membros;
    }

    public List<Registroduplicata> getDuplicatasEncontradas() {
        return duplicatasEncontradas;
    }

    //Importador múltiplo de arquivos CSV
    public void importarCSV(File arquivo) throws IOException {
    // Especificar UTF-8 garante a correta leitura de acentos no Windows/Linux
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
        String linha;
        int numeroLinha = 0;

        while ((linha = br.readLine()) != null) {
            numeroLinha++;
            
            // Ignora linhas vazias
            if (linha.trim().isEmpty()) {
                continue;
            }

            String[] dados = linha.split(";");
            if (dados.length < 5) {
                continue;
            }

            try {
                String tipo = dados[0].trim();
                String nome = dados[1].trim();
                String cpfBruto = dados[2].trim(); 
                int idade = Integer.parseInt(dados[3].trim());
                String cpfLimpo = cpfBruto.replaceAll("[^0-9]", "");

                // Verifica duplicidade pelo CPF numérico padronizado
                boolean jaExiste = membros.stream().anyMatch(p -> p.getCpf().replaceAll("[^0-9]", "").equals(cpfLimpo));

                if (jaExiste) {
                    duplicatasEncontradas.add(new Registroduplicata(arquivo.getName(), numeroLinha, cpfBruto));
                } else {
                    if (tipo.equalsIgnoreCase("Atleta")) {
                        String modalidade = dados[4].trim();
                        membros.add(new Atleta(nome, cpfLimpo, idade, modalidade));

                    } else if (tipo.equalsIgnoreCase("Instrutor")) {
                        // Garante que a linha possui o campo de salário antes de tentar ler dados[5]
                        if (dados.length < 6) {
                            continue;
                        }

                        String especialidade = dados[4].trim();
                        double salario = Double.parseDouble(dados[5].trim().replace(",", "."));
                        
                        // Corrigido para cpfLimpo, mantendo padrão com Atleta
                        membros.add(new Instrutor(nome, cpfLimpo, idade, especialidade, salario));
                    }
                }
            } catch (NumberFormatException e) {
                // Pula linhas com cabeçalhos ("Nome;CPF;Idade") ou dados corrompidos sem crashar o loop
                continue;
            }
        }
    }
}
    
    public List<Pessoa> buscarMulticriterios(String termo, String campo) {
        String termoBusca = termo.toLowerCase().trim();
       
        return membros.stream().filter(p -> {
                switch (campo.toLowerCase()) {
                    case "nome":
                        return p.getNome().toLowerCase().contains(termoBusca);

                    case "Sobrenome":
                        String[] partesNome = p.getNome().toLowerCase().split(" ");
                        if (partesNome.length > 1) {
                            return partesNome[partesNome.length - 1].toLowerCase().contains(termoBusca);
                        }
                        return false;

                    case "cpf":
                        return p.getCpf().contains(termoBusca.replaceAll("[^0-9]", ""));

                    case "idade":
                        return String.valueOf(p.getIdade()).equals(termoBusca);

                    case "modalidade":
                        if (p instanceof Atleta) {
                            return ((Atleta) p).getModalidade().toLowerCase().contains(termoBusca);
                        }
                        return false;

                    default:
                        return false;
                }
            }).collect(Collectors.toList());
    }

    public void adicionarMembro(Pessoa p) {
        String cpfLimpo = p.getCpf().replaceAll("[^0-9]", "");
        if (membros.stream().anyMatch(membro -> membro.getCpf().equals(cpfLimpo))) {
            throw new IllegalArgumentException("Já existe um membro com o CPF inserido: " + p.getCpf());
        }
        membros.add(p);
    }

    public boolean removerMembroPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return membros.removeIf(p -> p.getCpf().equals(cpfLimpo));
    }

    public void AlterarSalarioInstrutor(String cpf, double novoSalario) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        Pessoa p = membros.stream()
            .filter(membro -> membro.getCpf().equals(cpfLimpo) && membro instanceof Instrutor)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Instrutor com o CPF fornecido não encontrado: " + cpf));
        
        if (p instanceof Instrutor) {
            ((Instrutor) p).setSalario(novoSalario);
        }else {
            throw new IllegalArgumentException("O membro encontrado não é um instrutor: " + cpf);
        }
    } 

    public long getTotalMembros() {
        return membros.stream().count();
    }

    public long getTotalAtletas() {
        return membros.stream().filter(p -> p instanceof Atleta).count();
    }

    public long getTotalInstrutores() {
        return membros.stream().filter(p -> p instanceof Instrutor).count();
    }

    public double getSomaSalarios() {
        return membros.stream()
            .filter(p -> p instanceof Instrutor)
            .mapToDouble(p -> ((Instrutor) p).getSalario())
            .sum();
    }

    public double getMediaSalarios() {
        return membros.stream()
            .filter(p -> p instanceof Instrutor)
            .mapToDouble(p -> ((Instrutor) p).getSalario())
            .average()
            .orElse(0.0);
    }

    //public List<Pessoa> buscarPorTrechoCpf(String trechoCpf){
    //    String trechoLimpo = trechoCpf == null ? "" : trechoCpf.replaceAll("[^0-9]", "");
    //    List<Pessoa> encontrados = membros.stream()
    //        .filter(p -> p.getCpf().contains(trechoLimpo))
    //        .collect(Collectors.toList());
    //    if (encontrados.isEmpty()) {
    //        throw new PessoaNaoEncontradaException("Nenhuma pessoa encontrada com o trecho de CPF: " + trechoCpf);
    //    }
    //    return encontrados;
    //}

}