package accommodationfinder.ui;

import accommodationfinder.auth.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplicationPanel extends JPanel {

    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTextArea featuredListingsArea;

    private final UserService userService;
    private final MainWindow mainWindow;

    public MainApplicationPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        // Initialize UI components
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome to ResFinder!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        featuredListingsArea = new JTextArea("Featured Accommodation Listings will go here...\n(Placeholder)");
        featuredListingsArea.setEditable(false); // Make it read-only

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout(); // Call logout method
            }
        });

        // Add components to the main panel
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(featuredListingsArea), BorderLayout.CENTER);
        mainPanel.add(logoutButton, BorderLayout.SOUTH);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void logout() {
        mainWindow.saveJwtToPreferences(null); // Clear JWT from preferences

        mainWindow.switchToLoginPanel();
        JOptionPane.showMessageDialog(mainWindow, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }


}