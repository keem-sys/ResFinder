package accommodationfinder;

import accommodationfinder.ui.MainWindow;
import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {


        // Set Look and Feel using Nimbus L&F
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus Look and Feel not available, using default.");
        }

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
