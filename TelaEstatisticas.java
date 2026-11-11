import javax.swing.*;
import java.awt.*;

/** Janela auxiliar que mostra o painel de estatísticas da equipe. */
public class TelaEstatisticas extends JDialog {

    public TelaEstatisticas(JFrame janelaPrincipal, Equipe equipe) {
        super(janelaPrincipal, "Estatísticas da Equipe", true); // true = trava a janela principal enquanto aberta
        setLayout(new BorderLayout());

        JPanel painelDados = new JPanel(new GridLayout(0, 2, 20, 8));
        painelDados.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        adicionarLinha(painelDados, "Total de membros:", String.valueOf(equipe.getTotalMembros()));
        adicionarLinha(painelDados, "Total de atletas:", String.valueOf(equipe.getTotalAtletas()));
        adicionarLinha(painelDados, "Total de instrutores:", String.valueOf(equipe.getTotalInstrutores()));
        adicionarLinha(painelDados, "Soma dos salários:", String.format("R$ %.2f", equipe.getSomaSalarios()));
        adicionarLinha(painelDados, "Média dos salários:", String.format("R$ %.2f", equipe.getMediaSalarios()));
        adicionarLinha(painelDados, "Média de idade:", String.format("%.1f anos", equipe.getMediaIdade()));

        add(painelDados, BorderLayout.CENTER);

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());
        JPanel painelBotao = new JPanel();
        painelBotao.add(botaoFechar);
        add(painelBotao, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(janelaPrincipal); // abre centralizada sobre a janela principal
    }

    private void adicionarLinha(JPanel painel, String rotulo, String valor) {
        painel.add(new JLabel(rotulo));
        painel.add(new JLabel(valor));
    }
}
