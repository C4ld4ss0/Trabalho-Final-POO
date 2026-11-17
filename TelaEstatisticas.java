import javax.swing.*;
import java.awt.*;

public class TelaEstatisticas extends JDialog {

    public TelaEstatisticas(JFrame parent, Equipe equipe) {
        super(parent, "Estatísticas Gerais da Equipe", true);
        setSize(380, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelDados = new JPanel(new GridLayout(6, 2, 8, 8));
        painelDados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 20, 10, 20),
                BorderFactory.createTitledBorder("Resumo Geral")
        ));

        double mediaIdade = equipe.getMembros().stream()
                .mapToInt(Pessoa::getIdade)
                .average()
                .orElse(0.0);

        adicionarLinha(painelDados, "Total de membros:", String.valueOf(equipe.getTotalMembros()));
        adicionarLinha(painelDados, "Total de atletas:", String.valueOf(equipe.getTotalAtletas()));
        adicionarLinha(painelDados, "Total de instrutores:", String.valueOf(equipe.getTotalInstrutores()));
        adicionarLinha(painelDados, "Soma dos salários:", String.format("R$ %.2f", equipe.getSomaSalarios()));
        adicionarLinha(painelDados, "Média dos salários:", String.format("R$ %.2f", equipe.getMediaSalarios()));
        adicionarLinha(painelDados, "Média de idade:", String.format("%.1f anos", mediaIdade));

        add(painelDados, BorderLayout.CENTER);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());
        painelBotao.add(botaoFechar);

        add(painelBotao, BorderLayout.SOUTH);
    }

    private void adicionarLinha(JPanel painel, String rotulo, String valor) {
        JLabel lblRotulo = new JLabel(rotulo);
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(lblValor.getFont().deriveFont(Font.BOLD));
        painel.add(lblRotulo);
        painel.add(lblValor);
    }
}
