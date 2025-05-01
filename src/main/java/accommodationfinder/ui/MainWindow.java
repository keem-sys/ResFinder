package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.data.AccommodationDao;
import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;
import accommodationfinder.data.DatabaseConnection;
import accommodationfinder.data.UserDao;

import javax.swing.*;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class MainWindow extends JFrame {
    private DatabaseConnection databaseConnection;
    private UserDao userDao;
    private UserService userService;
    private RegistrationPanel registrationPanel;
    private LoginPanel loginPanel;
    private MainApplicationPanel mainApplicationPanel;
    private AccommodationDetailPanel accommodationDetailPanel;

    private AccommodationDao accommodationDao;
    private AccommodationService accommodationService;

    private String currentJwtToken = null;
    private User currentUser = null;

    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        try {
            // Create DatabaseConnection instance
            databaseConnection = new DatabaseConnection();

            // Perform Database Initialization
            databaseConnection.initializeDatabase();

            // create DAOs
            userDao = new UserDao(databaseConnection);
            accommodationDao = new AccommodationDao(databaseConnection, userDao);

            // Create Services
            userService = new UserService(userDao);
            accommodationService = new AccommodationService(accommodationDao, userDao);

            // Initialize UI Panels
            this.mainApplicationPanel = new MainApplicationPanel(accommodationService, userService, this);
            this.registrationPanel = new RegistrationPanel(userService, this);
            this.loginPanel = new LoginPanel(userService, this);

            // Set initial content pane
            setContentPane(mainApplicationPanel.getMainPanel());

            // JWT Check
            String storedJwtToken = getJwtFromPreferences();
            System.out.println("Stored JWT on startup: " + (storedJwtToken != null ? "[PRESENT]" : "[NONE]"));

            if (storedJwtToken != null && !storedJwtToken.isEmpty()) {
                // Attempt to validate and log in using the stored token
                handleLoginSuccess(storedJwtToken, false);
            } else {
                System.out.println("No stored session token found. Starting as guest.");
                if (mainApplicationPanel != null) {
                    mainApplicationPanel.showLoggedOutState();
                }
            }


        } catch (SQLException e) {
            System.err.println("FATAL ERROR during application startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize the application database.\nPlease check logs or contact support.\nError: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if database setup fails critically
            return;
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
        setVisible(true);
    }

    // Handle successful login
    public void handleLoginSuccess(String jwtToken, boolean showSuccessMessage) {
        if (jwtToken == null) return;

        User user = userService.getUserFromToken(jwtToken);

        if (user != null) {
            this.currentJwtToken = jwtToken;
            this.currentUser = user;
            System.out.println("Login successful for user: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");

            // Update the UI in MainApplicationPanel
            if (mainApplicationPanel != null) {
                mainApplicationPanel.showLoggedInState(currentUser.getUsername());
            }

            // Navigate to the main application view
            showMainApplicationView();

            if (showSuccessMessage) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            System.err.println("Login success handled, but failed to retrieve user details from token.");
            // Clear invalid persisted token if userFetch failed
            if (getJwtFromPreferences() != null && getJwtFromPreferences().equals(jwtToken)) {
                saveJwtToPreferences(null); // Clear bad token
            }
            handleLogout(); // Revert to logged-out state
        }
    }

    // Handle logout
    public void handleLogout() {
        System.out.println("Handling logout.");
        this.currentJwtToken = null;
        this.currentUser = null;
        saveJwtToPreferences(null); // Clear persisted token

        // Update the UI in MainApplicationPanel
        if (mainApplicationPanel != null) {
            mainApplicationPanel.showLoggedOutState();
        }

        showMainApplicationView();
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
            prefs.remove("jwtToken");
        }
    }

    public void clearJwtFromPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
        prefs.remove(currentJwtToken);
        System.out.println("JWT token cleared from preferences.");
    }
    public void showMainApplicationView() {

        // Ensure the main panel itself exists
        if (mainApplicationPanel == null) {
            System.err.println("Error: MainApplicationPanel is null when trying to show it.");
            // TODO: Handle error exit or throw Exception
            return;
        }

        // Update the auth state just in case token expired between views
        if (currentUser != null) {
            mainApplicationPanel.showLoggedInState(currentUser.getUsername());
        } else {
            mainApplicationPanel.showLoggedOutState();
        }


        setContentPane(mainApplicationPanel.getMainPanel());
        revalidate();
        repaint();
        System.out.println("Switched to Main Application Panel");
    }


    /**
     * Switches the main window content to show the detailed view for a specific accommodation.
     * Fetches the accommodation data using the provided ID.
     *
     * @param accommodationId The ID of the accommodation to display.
     */
    public void switchToDetailedView(Long accommodationId) {
        System.out.println("Attempting to switch to detailed view for ID: " + accommodationId);
        try {
            Accommodation accommodation = accommodationService.getListingById(accommodationId);

            if (accommodation != null) {
                // Create a NEW instance of the detail panel each time
                accommodationDetailPanel = new AccommodationDetailPanel(accommodationService, this, accommodationId);
                setContentPane(accommodationDetailPanel.getDetailPanel());
                revalidate();
                repaint();
                System.out.println("Successfully switched to detailed view for: " + accommodation.getTitle());
            }
            else {
                // Handle case where listing is not found
                System.err.println("Accommodation with ID " + accommodationId + " not found.");
                JOptionPane.showMessageDialog(this,
                        "Could not find details for the selected accommodation.",
                        "Listing Not Found",
                        JOptionPane.WARNING_MESSAGE);
                // Switch back to main view
                showMainApplicationView();
            }

            // Update auth state in main panel BEFORE switching away
            if (mainApplicationPanel != null) {
                if (currentUser != null) mainApplicationPanel.showLoggedInState(currentUser.getUsername());
                else mainApplicationPanel.showLoggedOutState();
            }


        } catch (SQLException e) {
            // Handle database errors during fetch
            System.err.println("Database error fetching accommodation details for ID " + accommodationId + ": " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred while retrieving accommodation details.\nPlease try again later.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            // Switch back to main view
            showMainApplicationView();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentJwtToken() {
        return currentJwtToken;
    }



    // Getter for the main frame if needed by child components
    public JFrame getMainFrame() {
        return this;
    }



}
