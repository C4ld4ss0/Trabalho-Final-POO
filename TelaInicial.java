import javax.swing.*;
import java.awt.*;

/**
 * Tela de boas-vindas: é a primeira janela que aparece quando o programa abre.
 * Ao clicar em "Continuar" (ou apertar Enter), ela abre a janela principal com as abas
 * (TelaPrincipal) e se fecha. Aberta pela Main.
 */
public class TelaInicial extends JFrame {

    public TelaInicial() {
        super("Sistema de Gerenciamento da Equipe - POO");
        setSize(520, 320);
        setLocationRelativeTo(null);                      // centro da tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // fechar aqui encerra o programa
        setResizable(false);                              // tamanho fixo

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(35, 30, 30, 30));   // margem interna

        //-- Textos (centro): três linhas, uma embaixo da outra --//
        JPanel painelTextos = new JPanel(new GridLayout(3, 1, 0, 6));
        JLabel titulo = new JLabel("Gerenciamento da Equipe", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        JLabel subtitulo = new JLabel("Atletas, instrutores e finanças em um só lugar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JLabel disciplina = new JLabel("Trabalho Final de Programação Orientada a Objetos", SwingConstants.CENTER);
        disciplina.setForeground(Color.GRAY);
        painelTextos.add(titulo);
        painelTextos.add(subtitulo);
        painelTextos.add(disciplina);
        painel.add(painelTextos, BorderLayout.CENTER);

        //-- Botão (rodapé) --//
        JButton botaoContinuar = new JButton("Continuar");
        botaoContinuar.setFont(new Font("SansSerif", Font.BOLD, 15));
        botaoContinuar.setPreferredSize(new Dimension(160, 40));
        botaoContinuar.addActionListener(e -> abrirTelaPrincipal());
        JPanel painelBotao = new JPanel();
        painelBotao.add(botaoContinuar);
        painel.add(painelBotao, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoContinuar);   // apertar Enter também clica em "Continuar"

        add(painel);
        setVisible(true);
    }

    /** Botão "Continuar": abre a janela principal e fecha a tela inicial. */
    private void abrirTelaPrincipal() {
        new TelaPrincipal();   // abre a janela com as abas (ela carrega os dados sozinha)
        dispose();             // fecha só esta janela; o programa continua com a principal aberta
    }
}
