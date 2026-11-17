import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaBoasVindas extends JFrame {

    public TelaBoasVindas() {
        super("Bem-vindo - Sistema de Gerenciamento da Equipe");
        setSize(460, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout());

        // Painel central com título e subtítulo alinhados verticalmente
        JPanel painelTexto = new JPanel(new GridLayout(3, 1, 5, 5));
        painelTexto.setBorder(new EmptyBorder(30, 20, 15, 20));

        JLabel labelTitulo = new JLabel("Sistema de Gerenciamento Esportivo", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel labelSubtitulo = new JLabel("Controle de Atletas, Instrutores e Folha Salarial", SwingConstants.CENTER);
        labelSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel labelDisciplina = new JLabel("Trabalho Final - Programação Orientada a Objetos", SwingConstants.CENTER);
        labelDisciplina.setFont(new Font("SansSerif", Font.ITALIC, 12));

        painelTexto.add(labelTitulo);
        painelTexto.add(labelSubtitulo);
        painelTexto.add(labelDisciplina);

        add(painelTexto, BorderLayout.CENTER);

        // Painel inferior com o botão Continuar
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBorder(new EmptyBorder(10, 20, 25, 20));

        JButton botaoContinuar = new JButton("Continuar");
        botaoContinuar.setPreferredSize(new Dimension(140, 36));
        botaoContinuar.setFont(new Font("SansSerif", Font.BOLD, 14));

        botaoContinuar.addActionListener(e -> {
            new TelaPrincipal(); // Abre a janela principal com as abas
            dispose();           // Fecha a tela de boas-vindas
        });

        getRootPane().setDefaultButton(botaoContinuar); // Permite avançar apertando Enter
        painelBotao.add(botaoContinuar);

        add(painelBotao, BorderLayout.SOUTH);

        setVisible(true);
    }
}
