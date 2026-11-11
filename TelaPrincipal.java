import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela principal (JFrame) com duas abas:
 *  - Equipe: busca, tabela de membros, cadastro, remoção, duplicatas e importação de CSV.
 *  - Financeiro: resumo de salários, tabela dos instrutores, alterar salário e estatísticas.
 * A tela não guarda regras de negócio: ela só chama os métodos da Equipe e mostra o resultado.
 * Os botões funcionam por eventos: addActionListener diz qual método roda quando o botão é clicado.
 */
public class TelaPrincipal extends JFrame {
    private static final String ARQUIVO_DADOS = "equipe.csv"; // arquivo lido ao abrir e gravado a cada mudança

    private Equipe equipe;

    //-- Componentes da aba Equipe --//
    private JTable tabelaMembros;
    private DefaultTableModel modeloTabela;   // o "modelo" guarda as linhas; a JTable só desenha
    private JTextField campoBusca;
    private JComboBox<String> comboCampoBusca;
    // Pessoas mostradas na tabela, na mesma ordem das linhas: linha 3 da tabela = listaExibida.get(3).
    // Necessário porque a tabela pode estar mostrando só uma busca, e não a equipe inteira.
    private List<Pessoa> listaExibida = new ArrayList<>();

    //-- Componentes da aba Financeiro --//
    private JTable tabelaInstrutores;
    private DefaultTableModel modeloFinanceiro;
    private JLabel labelTotalInstrutores, labelSomaSalarios, labelMediaSalarios;
    private List<Instrutor> instrutoresExibidos = new ArrayList<>(); // mesma ideia da listaExibida

    /** Monta a janela, as duas abas e carrega os dados. Chamado pela Main. */
    public TelaPrincipal() {
        super("Sistema de Gerenciamento da Equipe - POO");   // título da janela
        this.equipe = new Equipe();

        setSize(1000, 600);
        setLocationRelativeTo(null);                          // null = centro da tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // fechar a janela encerra o programa

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Equipe", criarAbaEquipe());
        abas.addTab("Financeiro", criarAbaFinanceiro());
        add(abas);

        carregarDadosIniciais();
        setVisible(true);
    }

    // =====================================================================
    //  MONTAGEM DAS ABAS
    // =====================================================================

    /**
     * Monta a aba Equipe com BorderLayout: busca em cima (NORTH), tabela no meio (CENTER)
     * e botões embaixo (SOUTH). No fim, liga cada botão ao método que ele executa.
     */
    private JPanel criarAbaEquipe() {
        JPanel aba = new JPanel(new BorderLayout(10, 10));
        aba.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));   // margem interna

        //-- Busca (topo) --//
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));   // componentes lado a lado
        painelBusca.setBorder(BorderFactory.createTitledBorder("Busca"));   // moldura com título
        comboCampoBusca = new JComboBox<>(new String[]{"Nome", "Sobrenome", "CPF", "Idade", "Modalidade"});
        campoBusca = new JTextField(20);
        JButton botaoBuscar = new JButton("Buscar");
        JButton botaoListarTodos = new JButton("Listar Todos");
        painelBusca.add(new JLabel("Buscar por:"));
        painelBusca.add(comboCampoBusca);
        painelBusca.add(campoBusca);
        painelBusca.add(botaoBuscar);
        painelBusca.add(botaoListarTodos);
        aba.add(painelBusca, BorderLayout.NORTH);

        //-- Tabela (centro) --//
        modeloTabela = criarModeloTabela(new String[]{"Tipo", "Nome", "CPF", "Idade", "Modalidade / Especialidade"});
        tabelaMembros = criarTabela(modeloTabela);
        aba.add(new JScrollPane(tabelaMembros), BorderLayout.CENTER);   // JScrollPane = barra de rolagem

        //-- Ações (rodapé) --//
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelAcoes.setBorder(BorderFactory.createTitledBorder("Ações"));
        JButton botaoAdicionar = new JButton("Adicionar Pessoa");
        JButton botaoRemover = new JButton("Remover Pessoa");
        JButton botaoDuplicar = new JButton("Duplicar Pessoa");
        JButton botaoEncontrarDuplicadas = new JButton("Encontrar Duplicadas");
        JButton botaoImportarCSV = new JButton("Importar CSV");
        painelAcoes.add(botaoAdicionar);
        painelAcoes.add(botaoRemover);
        painelAcoes.add(botaoDuplicar);
        painelAcoes.add(botaoEncontrarDuplicadas);
        painelAcoes.add(botaoImportarCSV);
        aba.add(painelAcoes, BorderLayout.SOUTH);

        //-- Eventos: o que cada botão faz quando é clicado --//
        botaoBuscar.addActionListener(e -> buscar());
        campoBusca.addActionListener(e -> buscar());   // apertar Enter no campo também busca
        botaoListarTodos.addActionListener(e -> atualizarTabela(equipe.getMembros()));
        botaoAdicionar.addActionListener(e -> adicionarPessoa());
        botaoRemover.addActionListener(e -> removerPessoa());
        botaoDuplicar.addActionListener(e -> duplicarPessoa());
        botaoEncontrarDuplicadas.addActionListener(e -> encontrarDuplicadas());
        botaoImportarCSV.addActionListener(e -> importarCSV());

        return aba;
    }

    /** Monta a aba Financeiro. Mesma estrutura da criarAbaEquipe (ver explicação lá). */
    private JPanel criarAbaFinanceiro() {
        JPanel aba = new JPanel(new BorderLayout(10, 10));
        aba.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //-- Resumo (topo): 3 quadros lado a lado --//
        JPanel painelResumo = new JPanel(new GridLayout(1, 3, 10, 0));   // 1 linha, 3 colunas
        labelTotalInstrutores = new JLabel("", SwingConstants.CENTER);
        labelSomaSalarios = new JLabel("", SwingConstants.CENTER);
        labelMediaSalarios = new JLabel("", SwingConstants.CENTER);
        painelResumo.add(criarCartao("Instrutores", labelTotalInstrutores));
        painelResumo.add(criarCartao("Soma dos salários", labelSomaSalarios));
        painelResumo.add(criarCartao("Média salarial", labelMediaSalarios));
        aba.add(painelResumo, BorderLayout.NORTH);

        //-- Tabela de salários (centro) --//
        modeloFinanceiro = criarModeloTabela(new String[]{"Nome", "CPF", "Especialidade", "Salário"});
        tabelaInstrutores = criarTabela(modeloFinanceiro);
        JScrollPane rolagem = new JScrollPane(tabelaInstrutores);
        rolagem.setBorder(BorderFactory.createTitledBorder("Salários dos instrutores"));
        aba.add(rolagem, BorderLayout.CENTER);

        //-- Ações (rodapé) --//
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelAcoes.setBorder(BorderFactory.createTitledBorder("Ações"));
        JButton botaoAlterarSalario = new JButton("Alterar Salário");
        JButton botaoEstatisticas = new JButton("Estatísticas Gerais");
        painelAcoes.add(botaoAlterarSalario);
        painelAcoes.add(botaoEstatisticas);
        aba.add(painelAcoes, BorderLayout.SOUTH);

        //-- Eventos --//
        botaoAlterarSalario.addActionListener(e -> alterarSalario());
        botaoEstatisticas.addActionListener(e -> new TelaEstatisticas(this, equipe).setVisible(true));   // abre a janela auxiliar

        return aba;
    }

    /** Quadro com título e um valor em destaque. Usado nos 3 quadros do resumo do Financeiro. */
    private JPanel criarCartao(String titulo, JLabel valor) {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBorder(BorderFactory.createTitledBorder(titulo));
        valor.setFont(new Font("SansSerif", Font.BOLD, 20));
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }

    /**
     * Cria o modelo de uma tabela com as colunas informadas. Usado nas duas abas.
     * A classe anônima ({ ... } logo depois do new) sobrescreve isCellEditable para que o usuário
     * não edite as células direto: toda mudança passa pelos botões e pelas validações.
     */
    private DefaultTableModel criarModeloTabela(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {   // 0 = começa sem linhas
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Cria uma JTable com o mesmo visual nas duas abas (linhas maiores, cabeçalho em negrito). */
    private JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   // só uma linha selecionada por vez
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabela.setFillsViewportHeight(true);   // fundo branco mesmo quando há poucas linhas
        return tabela;
    }

    // =====================================================================
    //  AÇÕES DA ABA EQUIPE
    // =====================================================================

    /** Botão "Buscar" (ou Enter no campo): filtra pela Equipe.buscarMulticriterios e mostra o resultado. */
    private void buscar() {
        String termo = campoBusca.getText().trim();
        String campo = (String) comboCampoBusca.getSelectedItem();
        atualizarTabela(equipe.buscarMulticriterios(termo, campo));
    }

    /** Botão "Importar CSV": abre o seletor de arquivos (aceita vários) e importa cada um. */
    private void importarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;   // usuário cancelou

        try {
            for (File f : chooser.getSelectedFiles()) {
                equipe.importarCSV(f);
            }
            dadosAlterados();
            JOptionPane.showMessageDialog(this, "Importação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao importar os arquivos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Botão "Adicionar Pessoa": pergunta o tipo e depois cada campo, um por vez.
     * Cada campo é validado assim que é digitado (ver pedirCampo). Se o usuário cancelar
     * qualquer pergunta, o método retorna (return) e nada é cadastrado.
     */
    private void adicionarPessoa() {
        String[] opcoes = {"Atleta", "Instrutor"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Selecione o tipo de pessoa:", "Adicionar Pessoa",
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (tipo == null) return;

        boolean ehAtleta = tipo.equals("Atleta");
        String titulo = "Adicionar " + tipo;
        int idadeMin = ehAtleta ? Validador.IDADE_MIN_ATLETA : Validador.IDADE_MIN_INSTRUTOR;   // ternário: se atleta 10, senão 18

        // Validador::validarNome = "use o método validarNome para validar este campo"
        String nome = pedirCampo(titulo, "Digite o nome:", Validador::validarNome);
        if (nome == null) return;

        // Para o CPF, além de validar o formato, também verifica se ele já está cadastrado
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
            dadosAlterados();
            JOptionPane.showMessageDialog(this, tipo + " adicionado(a) com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Botão "Remover Pessoa": remove a pessoa da linha selecionada, depois de confirmar.
     * Remove pelo objeto (e não pelo CPF) para que, havendo cópias, só uma saia.
     */
    private void removerPessoa() {
        int linha = tabelaMembros.getSelectedRow();   // -1 quando nenhuma linha está selecionada
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pessoa selecionada = listaExibida.get(linha);
        int resposta = JOptionPane.showConfirmDialog(this, "Remover " + selecionada.getNome() + "?", "Confirmar remoção",
                JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) return;

        equipe.removerMembro(selecionada);
        dadosAlterados();
    }

    /** Botão "Duplicar Pessoa": cria uma cópia idêntica da pessoa selecionada (Equipe.duplicarMembro). */
    private void duplicarPessoa() {
        int linha = tabelaMembros.getSelectedRow();   // ver explicação em removerPessoa
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para duplicar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pessoa copia = equipe.duplicarMembro(listaExibida.get(linha));
        dadosAlterados();
        JOptionPane.showMessageDialog(this, "Cópia criada:\n" + copia.obterDadosFormatados(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Botão "Encontrar Duplicadas" (funcionalidade extra): mostra na tabela só quem tem cópia igual. */
    private void encontrarDuplicadas() {
        List<Pessoa> duplicadas = equipe.encontrarDuplicadas();
        if (duplicadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma pessoa duplicada na equipe.", "Encontrar Duplicadas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            atualizarTabela(duplicadas);
            JOptionPane.showMessageDialog(this, duplicadas.size() + " registro(s) duplicado(s) exibido(s) na tabela.",
                    "Encontrar Duplicadas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =====================================================================
    //  AÇÕES DA ABA FINANCEIRO
    // =====================================================================

    /**
     * Botão "Alterar Salário": pega o instrutor selecionado, pede o novo valor e chama setSalario,
     * que só aceita um valor maior que o atual (ver Instrutor.setSalario).
     */
    private void alterarSalario() {
        int linha = tabelaInstrutores.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um instrutor na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Instrutor instrutor = instrutoresExibidos.get(linha);
        String texto = JOptionPane.showInputDialog(this, "Novo salário de " + instrutor.getNome() + ":",
                "Alterar Salário", JOptionPane.QUESTION_MESSAGE);
        if (texto == null || texto.trim().isEmpty()) return;

        try {
            instrutor.setSalario(Validador.lerSalario(texto));
            dadosAlterados();
            JOptionPane.showMessageDialog(this, "Salário alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================================
    //  DADOS E ATUALIZAÇÃO DAS TELAS
    // =====================================================================

    /**
     * Chamado depois de qualquer mudança (importar, adicionar, remover, duplicar, alterar salário):
     * grava o arquivo e atualiza as duas abas.
     */
    private void dadosAlterados() {
        salvarDados();
        atualizarTabela(equipe.getMembros());
        atualizarFinanceiro();
    }

    /** Grava a equipe em equipe.csv (Equipe.salvarCSV), para nada se perder ao fechar o programa. */
    private void salvarDados() {
        try {
            equipe.salvarCSV(new File(ARQUIVO_DADOS));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Ao abrir: lê o equipe.csv; se ele não existir (ou estiver vazio), usa os dados de exemplo. */
    private void carregarDadosIniciais() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            try {
                equipe.importarCSV(arquivo);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao ler " + ARQUIVO_DADOS + ": " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (equipe.getMembros().isEmpty()) {
            equipe.carregarDadosExemplo();
        }
        atualizarTabela(equipe.getMembros());
        atualizarFinanceiro();
    }

    /**
     * Pergunta um campo e repete a pergunta enquanto o valor for inválido.
     * Recebe a função de validação como parâmetro (validador), por isso serve para qualquer campo.
     * <T> = tipo genérico: devolve String para nome, Integer para idade, Double para salário.
     * Retorna null se o usuário clicar em Cancelar. Usado só no adicionarPessoa.
     */
    private <T> T pedirCampo(String titulo, String pergunta, java.util.function.Function<String, T> validador) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(this, pergunta, titulo, JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) return null;
            try {
                return validador.apply(entrada);   // valor válido: sai do laço devolvendo o valor
            } catch (IllegalArgumentException ex) {
                // valor inválido: mostra o motivo e o while pergunta de novo
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Preenche a tabela da aba Equipe com a lista recebida (a equipe toda, uma busca ou as duplicadas).
     * instanceof verifica o tipo real do objeto para saber qual coluna mostrar (modalidade ou especialidade).
     */
    private void atualizarTabela(List<Pessoa> lista) {
        modeloTabela.setRowCount(0);   // apaga as linhas antigas
        listaExibida = new ArrayList<>(lista);

        for (Pessoa p : lista) {
            if (p instanceof Atleta) {
                Atleta a = (Atleta) p;   // cast: agora podemos chamar getModalidade()
                modeloTabela.addRow(new Object[]{"Atleta", a.getNome(), a.getCpfFormatado(), a.getIdade(), a.getModalidade()});
            } else if (p instanceof Instrutor) {
                Instrutor i = (Instrutor) p;
                modeloTabela.addRow(new Object[]{"Instrutor", i.getNome(), i.getCpfFormatado(), i.getIdade(), i.getEspecialidade()});
            }
        }
    }

    /** Preenche a tabela e os 3 quadros da aba Financeiro. Mesma ideia do atualizarTabela, só com instrutores. */
    private void atualizarFinanceiro() {
        modeloFinanceiro.setRowCount(0);
        instrutoresExibidos = new ArrayList<>();

        for (Pessoa p : equipe.getMembros()) {
            if (p instanceof Instrutor) {
                Instrutor i = (Instrutor) p;
                instrutoresExibidos.add(i);
                modeloFinanceiro.addRow(new Object[]{i.getNome(), i.getCpfFormatado(), i.getEspecialidade(),
                        String.format("R$ %.2f", i.getSalario())});
            }
        }

        labelTotalInstrutores.setText(String.valueOf(equipe.getTotalInstrutores()));
        labelSomaSalarios.setText(String.format("R$ %.2f", equipe.getSomaSalarios()));
        labelMediaSalarios.setText(String.format("R$ %.2f", equipe.getMediaSalarios()));
    }
}
