package accommodationfinder.ui;

import accommodationfinder.data.AccommodationDao;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;
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
    private MainApplicationPanel mainApplicationPanel;

    private AccommodationDao accommodationDao;
    private AccommodationService accommodationService;

    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        try {
            // Database Connection
            databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            // DAOs
            userDao = new UserDao(databaseConnection);
            accommodationDao = new AccommodationDao(databaseConnection, userDao);

            // Services
            userService = new UserService(userDao);
            accommodationService = new AccommodationService(accommodationDao, userDao);

        } catch (SQLException e) {
            System.err.println("Error initialising database connection " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initialising database",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        // Initialize UI Panels
        this.mainApplicationPanel = new MainApplicationPanel(userService, accommodationService, this);
        this.registrationPanel = new RegistrationPanel(userService, this);
        this.loginPanel = new LoginPanel(userService, this);

        // Initial Content Pane
        setContentPane(mainApplicationPanel.getMainPanel());

        boolean automaticLoginAttempted = false;

        // Attempt Login Check
        String storedJwtToken = getJwtFromPreferences();
        if (storedJwtToken != null && !storedJwtToken.isEmpty()) {
            if (userService.validateJwtToken(storedJwtToken)) {
                System.out.println("Automatic login successful (in background, for session persistence).");
                // TODO: Update UI elements based on logged-in state here
                // mainApplicationPanel.setUserLoggedIn(true); // Add such a method to MainApplicationPanel
            } else {
                System.out.println("Stored JWT invalid, starting in guest mode.");
            }
        } else {
            System.out.println("Starting in guest mode.");
        }





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

    public void showMainApplicationView() {
        setContentPane(mainApplicationPanel.getMainPanel());
        revalidate();
        repaint();
        System.out.println("Switched to Main Application Panel");
    }



}
