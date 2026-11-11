import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Equipe {
    private List<Pessoa> membros;

    public Equipe() {
        this.membros = new ArrayList<>();
    }

    public List<Pessoa> getMembros() {
        return membros;
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
                String cpf = dados[2].trim();
                int idade = Integer.parseInt(dados[3].trim());

                // Registros repetidos também são adicionados: o botão "Encontrar Duplicadas" é quem os aponta
                if (tipo.equalsIgnoreCase("Atleta")) {
                    String modalidade = dados[4].trim();
                    membros.add(new Atleta(nome, cpf, idade, modalidade));

                } else if (tipo.equalsIgnoreCase("Instrutor")) {
                    // Garante que a linha possui o campo de salário antes de tentar ler dados[5]
                    if (dados.length < 6) {
                        continue;
                    }

                    String especialidade = dados[4].trim();
                    double salario = Double.parseDouble(dados[5].trim().replace(",", "."));

                    membros.add(new Instrutor(nome, cpf, idade, especialidade, salario));
                }
            } catch (NumberFormatException e) {
                // Pula linhas com cabeçalhos ("Nome;CPF;Idade") ou dados corrompidos sem crashar o loop
                continue;
            } catch (IllegalArgumentException e) {
                // Linha reprovada pela validação (nome, CPF, idade, salário...)
                System.out.println("Linha " + numeroLinha + " de " + arquivo.getName() + " rejeitada: " + e.getMessage());
                continue;
            }
        }
    }
}
    
    /** Grava todos os membros no arquivo texto, uma pessoa por linha. */
    public void salvarCSV(File arquivo) throws IOException {
        try (PrintWriter escritor = new PrintWriter(arquivo, "UTF-8")) {
            membros.forEach(p -> escritor.println(p.paraLinhaCsv()));
        }
    }

    /** Dados usados quando o programa abre e ainda não existe arquivo salvo. */
    public void carregarDadosExemplo() {
        adicionarMembro(new Atleta("Luccas Silva", "10000000001", 22, "Natação"));
        adicionarMembro(new Atleta("Ana Santos", "10000000003", 19, "Vôlei"));
        adicionarMembro(new Atleta("Beatriz Souza", "10000000004", 24, "Basquete"));
        adicionarMembro(new Atleta("João Coutinho", "20000000104", 25, "Basquete"));
        adicionarMembro(new Instrutor("Marcos Oliveira", "10000000002", 35, "Musculação", 3200.00));
        adicionarMembro(new Instrutor("Carlos Pereira", "10000000005", 41, "Treinamento Funcional", 3850.50));
        adicionarMembro(new Instrutor("Luciana Camargo", "20000000102", 36, "Musculação", 3300.00));
    }

    public List<Pessoa> buscarMulticriterios(String termo, String campo) {
        String termoBusca = termo.toLowerCase().trim();
       
        return membros.stream().filter(p -> {
                switch (campo.toLowerCase()) {
                    case "nome":
                        return p.getNome().toLowerCase().contains(termoBusca);

                    case "sobrenome":
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

    public boolean existeCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return membros.stream().anyMatch(m -> m.getCpf().equals(cpfLimpo));
    }

    public void adicionarMembro(Pessoa p) {
        String cpfLimpo = p.getCpf().replaceAll("[^0-9]", "");
        if (membros.stream().anyMatch(membro -> membro.getCpf().equals(cpfLimpo))) {
            throw new IllegalArgumentException("Já existe um membro com o CPF inserido: " + p.getCpf());
        }
        membros.add(p);
    }

    /** Adiciona uma cópia idêntica do membro informado (permite criar duplicatas de propósito). */
    public Pessoa duplicarMembro(Pessoa original) {
        Pessoa copia = original.copiar();
        membros.add(copia);
        return copia;
    }

    /** Retorna todos os membros que possuem pelo menos um objeto igual (equals) na equipe. */
    public List<Pessoa> encontrarDuplicadas() {
        return membros.stream()
            .filter(p -> Collections.frequency(membros, p) > 1)
            .sorted(Comparator.comparing(Pessoa::getNome))
            .collect(Collectors.toList());
    }

    /** Remove apenas UMA ocorrência do objeto (importante quando existem duplicatas). */
    public boolean removerMembro(Pessoa p) {
        return membros.remove(p);
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

    public double getMediaIdade() {
        return membros.stream()
            .mapToInt(Pessoa::getIdade)
            .average()
            .orElse(0.0);
    }

    public double getMediaSalarios() {
        return membros.stream()
            .filter(p -> p instanceof Instrutor)
            .mapToDouble(p -> ((Instrutor) p).getSalario())
            .average()
            .orElse(0.0);
    }
}