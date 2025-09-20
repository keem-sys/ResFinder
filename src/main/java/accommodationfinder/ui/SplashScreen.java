package accommodationfinder.ui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(253, 251, 245));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Add your logo or a welcome message
        JLabel welcomeLabel = new JLabel("Welcome to ResFinder", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);


        // Add a "loading" message at the bottom
        JLabel statusLabel = new JLabel("Initializing application...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(contentPanel);

        setSize(1280, 720);
        setLocationRelativeTo(null);
    }
}