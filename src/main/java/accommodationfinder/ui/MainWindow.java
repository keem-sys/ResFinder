package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import accommodationfinder.data.DatabaseConnection;
import accommodationfinder.data.UserDao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private DatabaseConnection databaseConnection;
    private UserDao userDao;
    private UserService userService;
    private RegistrationPanel registrationPanel;
    private LoginPanel loginPanel;

    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Attempt Login on Startup
        String storedJwtToken = getJwtFromPreferences();
        if (storedJwtToken != null && !storedJwtToken.isEmpty()) {
            System.out.println("Found JWT in Preferences - Attempting to login...");

            if (validateJwtToken(storedJwtToken)) { // TODO: Implement JWT validation in UserService
                System.out.println("JWT Token valid - Automatic login successful!");

                // TODO: Implement code to switch to main application view directly (skip login panel)

                JOptionPane.showMessageDialog(this, "Automatic login successful!", "Login",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("Stored JWT Token invalid - Showing Login Panel.");
                initializeLoginPanel();
            }
        } else {
            System.out.println("No JWT found in Preferences - Showing Login Panel.");
            initializeLoginPanel();
        }


        try {
            databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            userDao = new UserDao(databaseConnection);
            userService = new UserService(userDao);
        } catch (SQLException e) {
            System.err.println("Error initialising database connection " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initialising database",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        this.registrationPanel = new RegistrationPanel(userService, this);
        this.loginPanel = new LoginPanel(userService, this);
        setContentPane(loginPanel.getLoginPanel());

        JLabel titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        setLocationRelativeTo(null);
    }

    //   method to initialize LoginPanel and set content pane
    private void initializeLoginPanel() {
        loginPanel = new LoginPanel(userService, this);
        setContentPane(loginPanel.getLoginPanel());
    }

    private String getJwtFromPreferences() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
        return prefs.get("jwtToken", null);
    }

    // Placeholder for now for JWT Validation Method
    private boolean validateJwtToken(String jwtToken) {
        // TODO: Implement JWT validation logic in UserService (UserService.validateJwtToken(jwtToken))
        System.out.println("Warning: JWT validation is NOT yet implemented! assuming JWT is valid.");
        return true;
    }


    public void switchToRegistrationPanel() {
        setContentPane(registrationPanel.getRegistrationPanel());
        revalidate();
        repaint();
        System.out.println("Switched to registration panel");
    }

    public void switchToLoginPanel() {
        setContentPane(loginPanel.getLoginPanel());
        revalidate();
        repaint();
        System.out.println("Switched to login panel");
    }




}
