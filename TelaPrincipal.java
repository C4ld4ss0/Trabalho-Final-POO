import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TelaPrincipal extends JFrame {
    private Equipe equipe;

    // Componentes da Aba Equipe
    private JTable tabelaMembros;
    private DefaultTableModel modeloTabelaMembros;
    private JTextField campoBusca;
    private JComboBox<String> comboCampoBusca;
    private JButton botaoImportarCSV, botaoBuscar, botaoListarTodos,
                    botaoAdicionar, botaoRemover, botaoDuplicar, botaoVerDuplicatas;

    // Componentes da Aba Financeiro
    private JTable tabelaSalarios;
    private DefaultTableModel modeloTabelaSalarios;
    private JLabel labelQtdInstrutores, labelSomaSalarios, labelMediaSalarios;
    private JButton botaoAlterarSalario, botaoEstatisticas;

    public TelaPrincipal() {
        super("Sistema de Gerenciamento da Equipe - POO");
        this.equipe = new Equipe();

        setSize(980, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout());

        // Criação do JTabbedPane com as duas abas
        JTabbedPane abas = new JTabbedPane();
        abas.setBorder(new EmptyBorder(8, 8, 8, 8));
        abas.addTab("Equipe", criarPainelEquipe());
        abas.addTab("Financeiro", criarPainelFinanceiro());

        add(abas, BorderLayout.CENTER);

        configurarEventos();
        atualizarInterfaceCompleta();

        setVisible(true);
    }

    // =========================================================================
    // MONTAGEM DA ABA 1: EQUIPE
    // =========================================================================
    private JPanel criarPainelEquipe() {
        JPanel painelEquipe = new JPanel(new BorderLayout(10, 10));
        painelEquipe.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Painel Superior: Busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Filtro e Pesquisa"));

        painelBusca.add(new JLabel("Buscar por:"));
        comboCampoBusca = new JComboBox<>(new String[]{"Nome", "Sobrenome", "CPF", "Idade", "Modalidade"});
        painelBusca.add(comboCampoBusca);

        campoBusca = new JTextField(20);
        painelBusca.add(campoBusca);

        botaoBuscar = new JButton("Buscar");
        painelBusca.add(botaoBuscar);

        botaoListarTodos = new JButton("Listar Todos");
        painelBusca.add(botaoListarTodos);

        painelEquipe.add(painelBusca, BorderLayout.NORTH);

        // 2. Painel Central: Tabela de Membros (sem coluna de salário para não poluir com N/A)
        String[] colunasMembros = {"Tipo", "Nome", "CPF", "Idade", "Modalidade / Especialidade"};
        modeloTabelaMembros = new DefaultTableModel(colunasMembros, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaMembros = new JTable(modeloTabelaMembros);
        tabelaMembros.setRowHeight(24);
        tabelaMembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaMembros.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollMembros = new JScrollPane(tabelaMembros);
        scrollMembros.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));
        painelEquipe.add(scrollMembros, BorderLayout.CENTER);

        // 3. Painel Inferior: Ações da Equipe
        JPanel painelAcoesEquipe = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        painelAcoesEquipe.setBorder(BorderFactory.createTitledBorder("Ações da Equipe"));

        botaoAdicionar = new JButton("Adicionar Pessoa");
        botaoRemover = new JButton("Remover Pessoa");
        botaoDuplicar = new JButton("Duplicar Pessoa");
        botaoVerDuplicatas = new JButton("Encontrar Duplicadas");
        botaoImportarCSV = new JButton("Importar CSV");

        painelAcoesEquipe.add(botaoAdicionar);
        painelAcoesEquipe.add(botaoRemover);
        painelAcoesEquipe.add(botaoDuplicar);
        painelAcoesEquipe.add(botaoVerDuplicatas);
        painelAcoesEquipe.add(botaoImportarCSV);

        painelEquipe.add(painelAcoesEquipe, BorderLayout.SOUTH);

        return painelEquipe;
    }

    // =========================================================================
    // MONTAGEM DA ABA 2: FINANCEIRO
    // =========================================================================
    private JPanel criarPainelFinanceiro() {
        JPanel painelFinanceiro = new JPanel(new BorderLayout(10, 10));
        painelFinanceiro.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Painel Superior: Resumo Financeiro em 3 blocos (GridLayout)
        JPanel painelResumo = new JPanel(new GridLayout(1, 3, 15, 0));
        painelResumo.setBorder(BorderFactory.createTitledBorder("Resumo Financeiro Geral"));

        labelQtdInstrutores = new JLabel("0", SwingConstants.CENTER);
        labelSomaSalarios = new JLabel("R$ 0,00", SwingConstants.CENTER);
        labelMediaSalarios = new JLabel("R$ 0,00", SwingConstants.CENTER);

        painelResumo.add(criarBlocoIndicador("Quantidade de Instrutores", labelQtdInstrutores));
        painelResumo.add(criarBlocoIndicador("Soma dos Salários", labelSomaSalarios));
        painelResumo.add(criarBlocoIndicador("Média Salarial", labelMediaSalarios));

        painelFinanceiro.add(painelResumo, BorderLayout.NORTH);

        // 2. Painel Central: Tabela exclusiva de Instrutores e Salários
        String[] colunasSalarios = {"Nome", "CPF", "Idade", "Especialidade", "Salário"};
        modeloTabelaSalarios = new DefaultTableModel(colunasSalarios, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaSalarios = new JTable(modeloTabelaSalarios);
        tabelaSalarios.setRowHeight(24);
        tabelaSalarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaSalarios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollSalarios = new JScrollPane(tabelaSalarios);
        scrollSalarios.setBorder(BorderFactory.createTitledBorder("Folha Salarial dos Instrutores"));
        painelFinanceiro.add(scrollSalarios, BorderLayout.CENTER);

        // 3. Painel Inferior: Ações Financeiras
        JPanel painelAcoesFinanceiro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        painelAcoesFinanceiro.setBorder(BorderFactory.createTitledBorder("Ações Financeiras e Relatórios"));

        botaoAlterarSalario = new JButton("Alterar Salário");
        botaoEstatisticas = new JButton("Estatísticas Gerais");

        painelAcoesFinanceiro.add(botaoAlterarSalario);
        painelAcoesFinanceiro.add(botaoEstatisticas);

        painelFinanceiro.add(painelAcoesFinanceiro, BorderLayout.SOUTH);

        return painelFinanceiro;
    }

    private JPanel criarBlocoIndicador(String titulo, JLabel labelValor) {
        JPanel bloco = new JPanel(new BorderLayout());
        bloco.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(null, titulo, TitledBorder.CENTER, TitledBorder.TOP)
        ));
        labelValor.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelValor.setBorder(new EmptyBorder(10, 5, 10, 5));
        bloco.add(labelValor, BorderLayout.CENTER);
        return bloco;
    }

    // =========================================================================
    // EVENTOS DOS BOTÕES
    // =========================================================================
    private void configurarEventos() {
        // Importação de CSV
        botaoImportarCSV.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(".");
            chooser.setMultiSelectionEnabled(true);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File[] arquivos = chooser.getSelectedFiles();
                try {
                    for (File f : arquivos) {
                        equipe.importarCSV(f);
                    }
                    atualizarInterfaceCompleta();
                    JOptionPane.showMessageDialog(this, "Importação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao importar os arquivos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Busca Multicritérios
        Runnable acaoBuscar = () -> {
            String termo = campoBusca.getText().trim();
            String campo = (String) comboCampoBusca.getSelectedItem();
            try {
                List<Pessoa> resultado = equipe.buscarMulticriterios(termo, campo);
                atualizarTabelaMembros(resultado);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso na Busca", JOptionPane.WARNING_MESSAGE);
            }
        };
        botaoBuscar.addActionListener(e -> acaoBuscar.run());
        campoBusca.addActionListener(e -> acaoBuscar.run());

        // Listar Todos
        botaoListarTodos.addActionListener(e -> {
            campoBusca.setText("");
            atualizarTabelaMembros(equipe.getMembros());
        });

        // Adicionar Pessoa
        botaoAdicionar.addActionListener(e -> adicionarNovaPessoa());

        // Remover Pessoa
        botaoRemover.addActionListener(e -> {
            int linhaSel = tabelaMembros.getSelectedRow();
            if (linhaSel >= 0) {
                String nome = (String) modeloTabelaMembros.getValueAt(linhaSel, 1);
                String cpf = (String) modeloTabelaMembros.getValueAt(linhaSel, 2);
                int confirma = JOptionPane.showConfirmDialog(this,
                        "Deseja realmente remover " + nome + " (CPF: " + cpf + ")?",
                        "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    equipe.removerMembroPorCpf(cpf);
                    atualizarInterfaceCompleta();
                    JOptionPane.showMessageDialog(this, "Pessoa removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Duplicar Pessoa
        botaoDuplicar.addActionListener(e -> {
            int linhaSel = tabelaMembros.getSelectedRow();
            if (linhaSel >= 0) {
                String cpf = ((String) modeloTabelaMembros.getValueAt(linhaSel, 2)).replaceAll("[^0-9]", "");
                Pessoa selecionada = equipe.getMembros().stream()
                        .filter(p -> p.getCpf().equals(cpf))
                        .findFirst()
                        .orElse(null);
                if (selecionada != null) {
                    equipe.duplicarMembro(selecionada);
                    atualizarInterfaceCompleta();
                    JOptionPane.showMessageDialog(this, "Registro duplicado para fins de teste!", "Duplicar Pessoa", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para duplicar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Encontrar Duplicadas / Relatório de Duplicatas
        botaoVerDuplicatas.addActionListener(e -> {
            List<Pessoa> duplicadasLista = equipe.encontrarDuplicadasNaLista();
            List<Registroduplicata> duplicatasCSV = equipe.getDuplicatasEncontradas();

            if (!duplicadasLista.isEmpty()) {
                atualizarTabelaMembros(duplicadasLista);
                JOptionPane.showMessageDialog(this,
                        "Foram encontrados " + duplicadasLista.size() + " registros com CPF repetido na tabela.",
                        "Duplicadas Encontradas", JOptionPane.INFORMATION_MESSAGE);
            } else if (!duplicatasCSV.isEmpty()) {
                StringBuilder relatorio = new StringBuilder("Registros Duplicados ignorados no CSV:\n");
                duplicatasCSV.forEach(dup -> relatorio.append(dup.toString()).append("\n"));
                JOptionPane.showMessageDialog(this, relatorio.toString(), "Relatório de Duplicatas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma duplicata encontrada.", "Encontrar Duplicadas", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Alterar Salário (pela seleção na tabela da aba Financeiro ou digitando o CPF)
        botaoAlterarSalario.addActionListener(e -> alterarSalarioInstrutor());

        // Estatísticas Gerais (abre a janela auxiliar JDialog TelaEstatisticas)
        botaoEstatisticas.addActionListener(e -> {
            TelaEstatisticas dialogo = new TelaEstatisticas(this, equipe);
            dialogo.setVisible(true);
        });
    }

    private void adicionarNovaPessoa() {
        String[] opcoes = {"Atleta", "Instrutor"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Selecione o tipo de pessoa:", "Adicionar Pessoa",
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (tipo == null) return;

        boolean ehAtleta = tipo.equals("Atleta");
        String titulo = "Adicionar " + tipo;
        int idadeMin = ehAtleta ? Validador.IDADE_MIN_ATLETA : Validador.IDADE_MIN_INSTRUTOR;

        String nome = pedirCampo(titulo, "Digite o nome:", Validador::validarNome);
        if (nome == null) return;

        String cpf = pedirCampo(titulo, "Digite o CPF (11 números; pontos, traço e espaços são opcionais):", s -> {
            String limpo = Validador.validarCpf(s);
            if (equipe.existeCpf(limpo)) {
                throw new IllegalArgumentException("Já existe um membro cadastrado com este CPF.");
            }
            return limpo;
        });
        if (cpf == null) return;

        Integer idade = pedirCampo(titulo, "Digite a idade:",
                s -> Validador.validarIdade(Validador.lerIdade(s), idadeMin, Validador.IDADE_MAX));
        if (idade == null) return;

        try {
            if (ehAtleta) {
                String modalidade = pedirCampo(titulo, "Digite a modalidade:", s -> Validador.validarTexto(s, "modalidade"));
                if (modalidade == null) return;
                equipe.adicionarMembro(new Atleta(nome, cpf, idade, modalidade));
            } else {
                String especialidade = pedirCampo(titulo, "Digite a especialidade:", s -> Validador.validarTexto(s, "especialidade"));
                if (especialidade == null) return;
                Double salario = pedirCampo(titulo, "Digite o salário:", s -> Validador.validarSalario(Validador.lerSalario(s)));
                if (salario == null) return;
                equipe.adicionarMembro(new Instrutor(nome, cpf, idade, especialidade, salario));
            }
            atualizarInterfaceCompleta();
            JOptionPane.showMessageDialog(this, tipo + " adicionado(a) com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void alterarSalarioInstrutor() {
        int linhaSel = tabelaSalarios.getSelectedRow();
        String cpfAlvo;
        String nomeInstrutor = "";

        if (linhaSel >= 0) {
            nomeInstrutor = (String) modeloTabelaSalarios.getValueAt(linhaSel, 0);
            cpfAlvo = (String) modeloTabelaSalarios.getValueAt(linhaSel, 1);
        } else {
            cpfAlvo = JOptionPane.showInputDialog(this, "Selecione um instrutor na tabela ou digite o CPF do instrutor:");
            if (cpfAlvo == null || cpfAlvo.trim().isEmpty()) return;
        }

        String mensagem = nomeInstrutor.isEmpty()
                ? "Digite o novo salário para o CPF " + cpfAlvo + ":"
                : "Digite o novo salário para " + nomeInstrutor + ":";

        String salarioStr = JOptionPane.showInputDialog(this, mensagem, "Alterar Salário", JOptionPane.QUESTION_MESSAGE);
        if (salarioStr != null && !salarioStr.trim().isEmpty()) {
            try {
                double novoSalario = Validador.lerSalario(salarioStr);
                equipe.AlterarSalarioInstrutor(cpfAlvo, novoSalario);
                atualizarInterfaceCompleta();
                JOptionPane.showMessageDialog(this, "Salário alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Alterar Salário", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private <T> T pedirCampo(String titulo, String pergunta, java.util.function.Function<String, T> validador) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(this, pergunta, titulo, JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) return null;
            try {
                return validador.apply(entrada);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // =========================================================================
    // ATUALIZAÇÃO DAS TABELAS E INDICADORES
    // =========================================================================
    private void atualizarInterfaceCompleta() {
        atualizarTabelaMembros(equipe.getMembros());
        atualizarAbaFinanceiro();
    }

    private void atualizarTabelaMembros(List<Pessoa> lista) {
        modeloTabelaMembros.setRowCount(0);
        if (lista == null) return;

        for (Pessoa p : lista) {
            if (p instanceof Atleta) {
                Atleta a = (Atleta) p;
                modeloTabelaMembros.addRow(new Object[]{
                        "Atleta", a.getNome(), a.getCpfFormatado(), a.getIdade(), a.getModalidade()
                });
            } else if (p instanceof Instrutor) {
                Instrutor i = (Instrutor) p;
                modeloTabelaMembros.addRow(new Object[]{
                        "Instrutor", i.getNome(), i.getCpfFormatado(), i.getIdade(), i.getEspecialidade()
                });
            }
        }
    }

    private void atualizarAbaFinanceiro() {
        modeloTabelaSalarios.setRowCount(0);

        List<Instrutor> instrutores = equipe.getMembros().stream()
                .filter(p -> p instanceof Instrutor)
                .map(p -> (Instrutor) p)
                .collect(Collectors.toList());

        for (Instrutor i : instrutores) {
            modeloTabelaSalarios.addRow(new Object[]{
                    i.getNome(),
                    i.getCpfFormatado(),
                    i.getIdade(),
                    i.getEspecialidade(),
                    String.format("R$ %.2f", i.getSalario())
            });
        }

        labelQtdInstrutores.setText(String.valueOf(equipe.getTotalInstrutores()));
        labelSomaSalarios.setText(String.format("R$ %.2f", equipe.getSomaSalarios()));
        labelMediaSalarios.setText(String.format("R$ %.2f", equipe.getMediaSalarios()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal());
    }
}
