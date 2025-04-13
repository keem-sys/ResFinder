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
        setLocationRelativeTo(null); // Center the window

        try {
            // 1. Create DatabaseConnection instance
            databaseConnection = new DatabaseConnection();

            // 2. Perform ONE-TIME Database Initialization
            databaseConnection.initializeDatabase(); // <--- NEW: Call this first!

            // 3. Now create DAOs - they will use the simple getConnection() internally
            userDao = new UserDao(databaseConnection);
            accommodationDao = new AccommodationDao(databaseConnection, userDao); // Pass dependencies

            // 4. Create Services
            userService = new UserService(userDao);
            accommodationService = new AccommodationService(accommodationDao, userDao); // Pass dependencies

            // 5. Initialize UI Panels (pass the ready services)
            // Ensure MainApplicationPanel constructor uses the correct order if needed
            this.mainApplicationPanel = new MainApplicationPanel(accommodationService, userService, this);
            this.registrationPanel = new RegistrationPanel(userService, this);
            this.loginPanel = new LoginPanel(userService, this);

            // 6. Set initial content pane
            setContentPane(mainApplicationPanel.getMainPanel());

            // 7. JWT Check (no changes needed here)
            String storedJwtToken = getJwtFromPreferences();
            if (storedJwtToken != null && !storedJwtToken.isEmpty()) {
                if (userService.validateJwtToken(storedJwtToken)) {
                    System.out.println("Automatic login successful (session persisted).");
                    // Update UI state if needed (e.g., show username, hide login/signup)
                } else {
                    System.out.println("Stored JWT invalid or expired.");
                    // Optionally clear the invalid token
                    // saveJwtToPreferences(null);
                }
            } else {
                System.out.println("No stored session token found. Starting as guest.");
            }


        } catch (SQLException e) {
            System.err.println("FATAL ERROR during application startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize the application database.\nPlease check logs or contact support.\nError: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if database setup fails critically
            return; // Keep compiler happy
        } catch (Exception e) {
            // Catch other potential startup errors
            System.err.println("FATAL ERROR during application startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred during startup.\nError: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        // Removed the duplicate title label - the panels handle their own titles
        // setVisible(true); // Make the window visible at the end of the constructor
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
