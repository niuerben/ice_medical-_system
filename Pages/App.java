import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// App启动类
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Windows 外观（如果不可用就用系统默认）
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored2) {}
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
