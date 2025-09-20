package accommodationfinder;

import accommodationfinder.ui.MainWindow;
import javax.swing.*;

import accommodationfinder.ui.SplashScreen;
import com.formdev.flatlaf.FlatLightLaf;

public class MainApp {

    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.focusWidth",1);
            UIManager.put("TextComponent.arc",6);
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf Look and Feel, falling back to default: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system Look and Feel: " + ex.getMessage());
            }
        }

        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        SwingWorker<MainWindow, Void> worker = new SwingWorker<>() {
            @Override
            protected MainWindow doInBackground() throws Exception {
                System.out.println("Starting background initialization...");
                MainWindow mainWindow = new MainWindow();
                System.out.println("Background initialization complete.");
                return mainWindow;
            }

            @Override
            protected void done() {
                try {
                    MainWindow mainWindow = get();

                    splash.setVisible(false);
                    splash.dispose();

                    mainWindow.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "A fatal error occurred during startup: " + e.getMessage(),
                            "Startup Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        };

        worker.execute();
    }
}
