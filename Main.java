import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de partida do programa: é a classe que o Java executa primeiro.
 * Ela só configura o visual e abre a tela inicial (TelaInicial).
 */
public class Main {
    public static void main(String[] args) {
        // Troca o visual padrão do Swing pelo "Nimbus", que já vem com o Java e é mais moderno.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Se o Nimbus não estiver disponível, o programa segue com o visual padrão.
        }

        // invokeLater: o Swing exige que as janelas sejam criadas na "thread de eventos" dele.
        // Na prática: é a forma correta de abrir uma janela Swing.
        // O programa começa pela tela de boas-vindas; ela abre a TelaPrincipal ao clicar em "Continuar".
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }
}
