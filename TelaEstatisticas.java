import javax.swing.*;
import java.awt.*;

/**
 * Janela auxiliar (JDialog) com o painel de estatísticas da equipe.
 * Aberta pelo botão "Estatísticas Gerais" da aba Financeiro.
 * Os números vêm dos métodos de estatística da classe Equipe.
 */
public class TelaEstatisticas extends JDialog {

    public TelaEstatisticas(JFrame janelaPrincipal, Equipe equipe) {
        // true = janela "modal": a principal fica travada até esta ser fechada
        super(janelaPrincipal, "Estatísticas da Equipe", true);
        setLayout(new BorderLayout());

        // GridLayout(0, 2): duas colunas (rótulo | valor) e quantas linhas forem necessárias
        JPanel painelDados = new JPanel(new GridLayout(0, 2, 20, 8));
        painelDados.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));   // margem interna

        adicionarLinha(painelDados, "Total de membros:", String.valueOf(equipe.getTotalMembros()));
        adicionarLinha(painelDados, "Total de atletas:", String.valueOf(equipe.getTotalAtletas()));
        adicionarLinha(painelDados, "Total de instrutores:", String.valueOf(equipe.getTotalInstrutores()));
        adicionarLinha(painelDados, "Soma dos salários:", String.format("R$ %.2f", equipe.getSomaSalarios()));
        adicionarLinha(painelDados, "Média dos salários:", String.format("R$ %.2f", equipe.getMediaSalarios()));
        adicionarLinha(painelDados, "Média de idade:", String.format("%.1f anos", equipe.getMediaIdade()));

        add(painelDados, BorderLayout.CENTER);

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());   // dispose fecha só esta janela
        JPanel painelBotao = new JPanel();
        painelBotao.add(botaoFechar);
        add(painelBotao, BorderLayout.SOUTH);

        pack();                                   // ajusta o tamanho da janela ao conteúdo
        setLocationRelativeTo(janelaPrincipal);   // abre centralizada sobre a janela principal
    }

    /** Coloca uma linha "rótulo | valor" no painel. */
    private void adicionarLinha(JPanel painel, String rotulo, String valor) {
        painel.add(new JLabel(rotulo));
        painel.add(new JLabel(valor));
    }
}
