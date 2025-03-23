package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import accommodationfinder.data.DatabaseConnection;
import accommodationfinder.data.UserDao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.prefs.Preferences;

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

        // Attempt Login on Startup
        String storedJwtToken = getJwtFromPreferences();
        if (storedJwtToken != null && !storedJwtToken.isEmpty()) {
            System.out.println("Found JWT in Preferences - Attempting to login...");

            if (userService.validateJwtToken(storedJwtToken)) {
                System.out.println("JWT Token valid - Automatic login successful!");

                // TODO: Implement code to switch to main application view directly (skip login panel)

                JOptionPane.showMessageDialog(this, "Automatic login successful!", "Login",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                System.out.println("Stored JWT Token invalid - Showing Login Panel.");
                switchToLoginPanel();
            }
        } else {
            System.out.println("No JWT found in Preferences - Showing Login Panel.");
            switchToLoginPanel();
        }

        /*
        if (getContentPane() == null) { // If no panel was set (no auto-login)
            switchToMainPanel();
        }

         */



        setContentPane(loginPanel.getLoginPanel());

        JLabel titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        setLocationRelativeTo(null);
    }

    private String getJwtFromPreferences() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
        return prefs.get("jwtToken", null);
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

    public void saveJwtToPreferences(String jwtToken) {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        if (jwtToken != null) {
            prefs.put("jwtToken", jwtToken);
        } else {
            prefs.remove("jwtToken"); // Remove if null (logout)
        }
    }



}
