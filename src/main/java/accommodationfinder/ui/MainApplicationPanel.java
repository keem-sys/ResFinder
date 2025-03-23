package accommodationfinder.ui;

import accommodationfinder.auth.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplicationPanel {

    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTextArea featuredListingsArea;
    private JButton loginButton; // New Login Button
    private JButton registerButton; // New Register Button

    private final UserService userService;
    private final MainWindow mainWindow;

    public MainApplicationPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome to ResFinder!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        featuredListingsArea = new JTextArea("Featured Accommodation Listings will go here...\n(Placeholder)");
        featuredListingsArea.setEditable(false);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        loginButton = new JButton("Login"); // Initialize Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.switchToLoginPanel();
            }
        });

        registerButton = new JButton("Register"); // Initialize Register Button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.switchToRegistrationPanel(); // Redirect to RegistrationPanel in MainWindow
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for buttons
        buttonPanel.add(loginButton);      // Add Login Button to button panel
        buttonPanel.add(registerButton);   // Add Register Button to button panel


        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(featuredListingsArea), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(logoutButton, BorderLayout.EAST);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void logout() {
        mainWindow.saveJwtToPreferences(null);
        mainWindow.switchToLoginPanel();
        JOptionPane.showMessageDialog(mainWindow, "Logged out successfully.", "Logout",
                JOptionPane.INFORMATION_MESSAGE);
    }
}