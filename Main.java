import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém o visual padrão caso o Look and Feel do sistema não esteja disponível
        }
        SwingUtilities.invokeLater(() -> new TelaPrincipal());
    }
}
