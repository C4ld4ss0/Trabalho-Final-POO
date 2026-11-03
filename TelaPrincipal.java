
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
//
public class TelaPrincipal extends JFrame {
    private Equipe equipe;
    
    private JTable tabelaMembros; 
    private DefaultTableModel modeloTabela; 
    private JTextField campoBusca; 
    private JComboBox<String> comboCampoBusca;
    private JButton botaoImportarCSV, botaoBuscar, botaoListarTodos, 
                    botaoAdicionar, botaoRemover, botaoAlterarSalario, 
                    botaoVerDuplicatas, botaoEstatisticas;

    public TelaPrincipal() { 
        super("Sistema de Gerenciamento da Equipe - POO"); 
        this.equipe = new Equipe();

        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //-- PAINEL SUPERIOR --//
        JPanel painelSuperior = new JPanel(new FlowLayout());
        botaoImportarCSV = new JButton("Importar CSV");
        painelSuperior.add(botaoImportarCSV);  
        
        painelSuperior.add(new JLabel("Buscar por:"));
        comboCampoBusca = new JComboBox<>(new String[]{"Nome", "Sobrenome", "CPF", "Idade", "Modalidade"});
        painelSuperior.add(comboCampoBusca);

        campoBusca = new JTextField(15);
        painelSuperior.add(campoBusca);

        botaoBuscar = new JButton("Buscar");
        painelSuperior.add(botaoBuscar);

        botaoListarTodos = new JButton("Listar Todos");
        painelSuperior.add(botaoListarTodos);

        add(painelSuperior, BorderLayout.NORTH);
        //-- FIM DO PAINEL SUPERIOR --//

        //-- PAINEL CENTRAL --//
        // Corrigido para 6 colunas para bater com os 6 dados inseridos no atualizarTabela
        String[] colunas = {"Tipo", "Nome", "CPF", "Idade", "Modalidade / Especialidade", "Salário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede que o usuário edite o texto direto na tabela sem passar pelas validações
            }
        };
        tabelaMembros = new JTable(modeloTabela);
        add(new JScrollPane(tabelaMembros), BorderLayout.CENTER);
        //-- FIM DO PAINEL CENTRAL --//

        //-- PAINEL INFERIOR --//
        JPanel painelInferior = new JPanel(new FlowLayout());

        botaoAdicionar = new JButton("Adicionar Pessoa");
        botaoAlterarSalario = new JButton("Alterar Salário");
        botaoRemover = new JButton("Remover Pessoa");
        botaoVerDuplicatas = new JButton("Relatório das Duplicatas");
        botaoEstatisticas = new JButton("Estatísticas");
        
        painelInferior.add(botaoAdicionar);
        painelInferior.add(botaoAlterarSalario);
        painelInferior.add(botaoRemover);
        painelInferior.add(botaoVerDuplicatas);
        painelInferior.add(botaoEstatisticas);

        add(painelInferior, BorderLayout.SOUTH);
        //-- FIM DO PAINEL INFERIOR --//

        //-- IMPORTAÇÃO DE CSV --//
        botaoImportarCSV.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);

            int res = chooser.showOpenDialog(this);

            if (res == JFileChooser.APPROVE_OPTION) {
                File[] arquivos = chooser.getSelectedFiles();
                try {
                    for (File f : arquivos) {
                        equipe.importarCSV(f);
                    }
                    atualizarTabela(equipe.getMembros());
                    JOptionPane.showMessageDialog(this, "Importação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao importar os arquivos: " + ex.getMessage(), "Erro durante a importação", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //-- BUSCA MULTICRITÉRIOS --//
        botaoBuscar.addActionListener(e -> {
            String termo = campoBusca.getText().trim();
            String campo = (String) comboCampoBusca.getSelectedItem();
            try {
                List<Pessoa> resultado = equipe.buscarMulticriterios(termo, campo);
                atualizarTabela(resultado);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso na Busca", JOptionPane.WARNING_MESSAGE);
            }
        });

        //-- LISTAR TODOS --//
        botaoListarTodos.addActionListener(e -> atualizarTabela(equipe.getMembros()));

        //-- ALTERAR SALÁRIO --//
        botaoAlterarSalario.addActionListener(e -> {
            String cpf = JOptionPane.showInputDialog(this, "Digite o CPF do instrutor:");
            if (cpf != null && !cpf.trim().isEmpty()) {
                String salarioStr = JOptionPane.showInputDialog(this, "Digite o novo salário:");
                if (salarioStr != null && !salarioStr.trim().isEmpty()) {
                    try {
                        double novoSalario = Validador.lerSalario(salarioStr);
                        // Atenção: verifique se na classe Equipe o método está com 'A' maiúsculo ou 'a' minúsculo
                        equipe.AlterarSalarioInstrutor(cpf, novoSalario);
                        JOptionPane.showMessageDialog(this, "Salário alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        atualizarTabela(equipe.getMembros());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para o salário.", "Erro", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        //-- ADICIONAR PESSOA --//
        // Cada campo é validado assim que digitado (mesma ordem e mesmos avisos para Atleta e Instrutor)
        botaoAdicionar.addActionListener(e -> {
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
                atualizarTabela(equipe.getMembros());
                JOptionPane.showMessageDialog(this, tipo + " adicionado(a) com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Dado inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        //-- REMOVER PESSOA --//
        botaoRemover.addActionListener(e -> {
            int linhaSel = tabelaMembros.getSelectedRow();
            if (linhaSel >= 0) {
                String cpf = (String) modeloTabela.getValueAt(linhaSel, 2); // Coluna 2 agora é de fato o CPF
                try {
                    equipe.removerMembroPorCpf(cpf);
                    atualizarTabela(equipe.getMembros());
                    JOptionPane.showMessageDialog(this, "Pessoa removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma pessoa na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        //-- RELATÓRIO DE DUPLICATAS --//
        botaoVerDuplicatas.addActionListener(e -> {
            // Verifique se a sua classe se chama Registroduplicata ou RegistroDuplicata
            List<Registroduplicata> duplicatas = equipe.getDuplicatasEncontradas();
            if (duplicatas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma duplicata encontrada.", "Relatório de Duplicatas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder relatorio = new StringBuilder("Registros Duplicados:\n");
                duplicatas.forEach(dup -> relatorio.append(dup.toString()).append("\n"));
                JOptionPane.showMessageDialog(this, relatorio.toString(), "Relatório de Duplicatas", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //-- ESTATÍSTICAS --//
        botaoEstatisticas.addActionListener(e -> {
            String status = String.format(
                "-- Painel de Estatísticas --\n" +
                "Total de Membros: %d\n" +
                "Total de Atletas: %d\n" + 
                "Total de Instrutores: %d\n" +
                "Soma dos Salários: R$ %.2f\n" +
                "Média dos Salários dos Instrutores: R$ %.2f\n",
                equipe.getTotalMembros(),
                equipe.getTotalAtletas(),
                equipe.getTotalInstrutores(),
                equipe.getSomaSalarios(),
                equipe.getMediaSalarios()
            );
            JOptionPane.showMessageDialog(this, status, "Estatísticas da Equipe", JOptionPane.INFORMATION_MESSAGE);
        });
        
        setVisible(true);
    }

    /** Pede um campo ao usuário e repete enquanto o valor for inválido. Retorna null se cancelar. */
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

    private void atualizarTabela(List<Pessoa> lista) {
        modeloTabela.setRowCount(0); // Limpa a tabela
        if (lista == null) return;
        
        for (Pessoa p : lista) {
            if (p instanceof Atleta) {
                Atleta a = (Atleta) p;
                modeloTabela.addRow(new Object[]{"Atleta", a.getNome(), a.getCpfFormatado(), a.getIdade(), a.getModalidade(), "N/A"});
            } else if (p instanceof Instrutor) {
                Instrutor i = (Instrutor) p;
                modeloTabela.addRow(new Object[]{"Instrutor", i.getNome(), i.getCpfFormatado(), i.getIdade(), i.getEspecialidade(), String.format("R$ %.2f", i.getSalario())});
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal());
    }
}