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

/**
 * Guarda a coleção de membros (Atletas e Instrutores juntos em um List<Pessoa>) e reúne
 * todas as operações sobre ela: arquivo, busca, cadastro, duplicatas e estatísticas.
 * A tela (TelaPrincipal) nunca mexe na lista direto: sempre chama um método daqui.
 */
public class Equipe {
    private List<Pessoa> membros;

    public Equipe() {
        this.membros = new ArrayList<>();
    }

    /** Lista completa de membros. Usada pela TelaPrincipal para preencher as tabelas. */
    public List<Pessoa> getMembros() {
        return membros;
    }

    // =====================================================================
    //  ARQUIVOS
    // =====================================================================

    /**
     * Lê um arquivo CSV e adiciona cada linha válida como Atleta ou Instrutor.
     * Formato de cada linha: tipo;nome;cpf;idade;modalidade  ou  tipo;nome;cpf;idade;especialidade;salario
     * Usado ao abrir o programa (lê o equipe.csv) e no botão "Importar CSV".
     * Registros repetidos também entram: quem os aponta é o botão "Encontrar Duplicadas".
     */
    public void importarCSV(File arquivo) throws IOException {
        // try-with-resources: o arquivo é fechado sozinho no fim do bloco.
        // UTF-8 garante que acentos (ç, ã, é) sejam lidos corretamente.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 0;

            // readLine devolve null quando o arquivo acaba
            while ((linha = br.readLine()) != null) {
                numeroLinha++;

                if (linha.trim().isEmpty()) {
                    continue;   // pula linha vazia
                }

                String[] dados = linha.split(";");   // corta a linha em pedaços onde tem ";"
                if (dados.length < 5) {
                    continue;   // linha incompleta
                }

                try {
                    String tipo = dados[0].trim();
                    String nome = dados[1].trim();
                    String cpf = dados[2].trim();
                    int idade = Integer.parseInt(dados[3].trim());

                    if (tipo.equalsIgnoreCase("Atleta")) {
                        String modalidade = dados[4].trim();
                        membros.add(new Atleta(nome, cpf, idade, modalidade));

                    } else if (tipo.equalsIgnoreCase("Instrutor")) {
                        if (dados.length < 6) {
                            continue;   // instrutor sem o campo de salário
                        }

                        String especialidade = dados[4].trim();
                        double salario = Double.parseDouble(dados[5].trim().replace(",", "."));

                        membros.add(new Instrutor(nome, cpf, idade, especialidade, salario));
                    }
                } catch (NumberFormatException e) {
                    // idade ou salário que não são números (ex.: linha de cabeçalho): pula a linha
                    continue;
                } catch (IllegalArgumentException e) {
                    // dado reprovado pelo Validador: avisa no console e pula a linha
                    System.out.println("Linha " + numeroLinha + " de " + arquivo.getName() + " rejeitada: " + e.getMessage());
                    continue;
                }
            }
        }
    }

    /**
     * Grava todos os membros no arquivo, uma pessoa por linha.
     * Cada objeto monta a própria linha com paraLinhaCsv() (polimorfismo).
     * Usado pela TelaPrincipal depois de toda alteração (salvamento automático).
     */
    public void salvarCSV(File arquivo) throws IOException {
        try (PrintWriter escritor = new PrintWriter(arquivo, "UTF-8")) {
            membros.forEach(p -> escritor.println(p.paraLinhaCsv()));
        }
    }

    /** Dados de exemplo usados quando o programa abre e ainda não existe equipe.csv. */
    public void carregarDadosExemplo() {
        adicionarMembro(new Atleta("Luccas Silva", "10000000001", 22, "Natação"));
        adicionarMembro(new Atleta("Ana Santos", "10000000003", 19, "Vôlei"));
        adicionarMembro(new Atleta("Beatriz Souza", "10000000004", 24, "Basquete"));
        adicionarMembro(new Atleta("João Coutinho", "20000000104", 25, "Basquete"));
        adicionarMembro(new Instrutor("Marcos Oliveira", "10000000002", 35, "Musculação", 3200.00));
        adicionarMembro(new Instrutor("Carlos Pereira", "10000000005", 41, "Treinamento Funcional", 3850.50));
        adicionarMembro(new Instrutor("Luciana Camargo", "20000000102", 36, "Musculação", 3300.00));
    }

    // =====================================================================
    //  BUSCA E CADASTRO
    // =====================================================================

    /**
     * Devolve os membros que combinam com o termo digitado, no campo escolhido no combo.
     * Nome e sobrenome comparam só o INÍCIO (startsWith); CPF e modalidade aceitam qualquer parte (contains).
     * stream().filter(...) passa cada pessoa pelo teste e collect junta as aprovadas em uma nova lista.
     * Usado pelo botão "Buscar" da aba Equipe.
     */
    public List<Pessoa> buscarMulticriterios(String termo, String campo) {
        String termoBusca = termo.toLowerCase().trim();

        return membros.stream().filter(p -> {
                switch (campo.toLowerCase()) {
                    case "nome":
                        // startsWith = "começa com": só traz quem tem o nome INICIANDO pelo texto digitado
                        // (antes era contains, que trazia a letra em qualquer parte, ex.: "l" achava "Carlos")
                        return p.getNome().toLowerCase().startsWith(termoBusca);

                    case "sobrenome":
                        // separa o nome nos espaços e compara só o começo da última parte (o sobrenome)
                        String[] partesNome = p.getNome().toLowerCase().split(" ");
                        if (partesNome.length > 1) {
                            return partesNome[partesNome.length - 1].startsWith(termoBusca);
                        }
                        return false;

                    case "cpf":
                        // replaceAll("[^0-9]", "") apaga tudo que não for número (pontos e traço)
                        return p.getCpf().contains(termoBusca.replaceAll("[^0-9]", ""));

                    case "idade":
                        return String.valueOf(p.getIdade()).equals(termoBusca);

                    case "modalidade":
                        // só atletas têm modalidade; instrutores ficam de fora
                        if (p instanceof Atleta) {
                            return ((Atleta) p).getModalidade().toLowerCase().contains(termoBusca);
                        }
                        return false;

                    default:
                        return false;
                }
            }).collect(Collectors.toList());
    }

    /**
     * true se já existe alguém com esse CPF.
     * Usado no cadastro (TelaPrincipal.adicionarPessoa) para avisar logo no campo do CPF.
     */
    public boolean existeCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return membros.stream().anyMatch(m -> m.getCpf().equals(cpfLimpo));
    }

    /**
     * Adiciona uma pessoa nova, bloqueando CPF repetido.
     * Usado no cadastro manual e no carregarDadosExemplo.
     * (Cópias de propósito entram por duplicarMembro, que não faz esse bloqueio.)
     */
    public void adicionarMembro(Pessoa p) {
        String cpfLimpo = p.getCpf().replaceAll("[^0-9]", "");
        if (membros.stream().anyMatch(membro -> membro.getCpf().equals(cpfLimpo))) {
            throw new IllegalArgumentException("Já existe um membro com o CPF inserido: " + p.getCpf());
        }
        membros.add(p);
    }

    // =====================================================================
    //  DUPLICATAS (funcionalidade extra) E REMOÇÃO
    // =====================================================================

    /**
     * Cria uma cópia idêntica do membro e a coloca na equipe.
     * copiar() é polimórfico: gera um Atleta ou um Instrutor conforme o original.
     * Usado pelo botão "Duplicar Pessoa".
     */
    public Pessoa duplicarMembro(Pessoa original) {
        Pessoa copia = original.copiar();
        membros.add(copia);
        return copia;
    }

    /**
     * Devolve todos os membros que têm pelo menos uma cópia igual na equipe, ordenados por nome.
     * Collections.frequency conta quantas vezes a pessoa aparece na lista usando o equals();
     * se aparecer mais de 1 vez, é duplicada.
     * Usado pelo botão "Encontrar Duplicadas".
     */
    public List<Pessoa> encontrarDuplicadas() {
        return membros.stream()
            .filter(p -> Collections.frequency(membros, p) > 1)
            .sorted(Comparator.comparing(Pessoa::getNome))
            .collect(Collectors.toList());
    }

    /**
     * Remove UMA ocorrência da pessoa (List.remove usa o equals e para no primeiro que achar).
     * Assim, quando existem cópias, só uma delas sai. Usado pelo botão "Remover Pessoa".
     */
    public boolean removerMembro(Pessoa p) {
        return membros.remove(p);
    }

    // =====================================================================
    //  ESTATÍSTICAS
    //  Usadas pela janela TelaEstatisticas e pelo resumo da aba Financeiro.
    // =====================================================================

    public long getTotalMembros() {
        return membros.stream().count();
    }

    /** filter + instanceof: mantém só os atletas e count conta quantos sobraram. */
    public long getTotalAtletas() {
        return membros.stream().filter(p -> p instanceof Atleta).count();
    }

    /** Mesma ideia do getTotalAtletas, para instrutores. */
    public long getTotalInstrutores() {
        return membros.stream().filter(p -> p instanceof Instrutor).count();
    }

    /** Mantém só os instrutores, transforma cada um no seu salário (mapToDouble) e soma. */
    public double getSomaSalarios() {
        return membros.stream()
            .filter(p -> p instanceof Instrutor)
            .mapToDouble(p -> ((Instrutor) p).getSalario())
            .sum();
    }

    /** Média de idade de todos os membros. orElse(0.0) evita erro quando a lista está vazia. */
    public double getMediaIdade() {
        return membros.stream()
            .mapToInt(Pessoa::getIdade)
            .average()
            .orElse(0.0);
    }

    /** Mesma ideia do getSomaSalarios, mas com average() no lugar de sum(). */
    public double getMediaSalarios() {
        return membros.stream()
            .filter(p -> p instanceof Instrutor)
            .mapToDouble(p -> ((Instrutor) p).getSalario())
            .average()
            .orElse(0.0);
    }
}
