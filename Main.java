import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Usa o visual "Nimbus", que já vem com o Java e é mais moderno que o padrão
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // se não conseguir, o programa continua com o visual padrão
        }
        SwingUtilities.invokeLater(() -> new TelaPrincipal());
    }
}
