package accommodationfinder;

import accommodationfinder.ui.MainWindow;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class MainApp {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.focusWidth",1);
            UIManager.put("TextComponent.arc",6);
        } catch (Exception e) {
            // Fallback to system L&F if FlatLaf fails (e.g., missing JAR)
            System.err.println("Failed to set FlatLaf Look and Feel, falling back to default: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system Look and Feel: " + ex.getMessage());
            }
        }

        // Run the MainWindow class set in UI using EDT throws Error if application start failed
        SwingUtilities.invokeLater(() -> {
            try{
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error creating main window: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting the application",
                        "Application Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
