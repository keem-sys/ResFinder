package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.data.AccommodationDao;
import accommodationfinder.data.SavedListingDAO;
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
    private SavedListingDAO savedListingDAO;
    private UserService userService;
    private RegistrationPanel registrationPanel;
    private LoginPanel loginPanel;
    private MainApplicationPanel mainApplicationPanel;
    private AccommodationDetailPanel accommodationDetailPanel;
    private ContactPanel contactPanel;
    private FaqPanel faqPanel;

    private AccommodationDao accommodationDao;
    private AccommodationService accommodationService;

    private MenuBarManager menuBarManager;

    private String currentJwtToken = null;
    private User currentUser = null;


    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            databaseConnection = new DatabaseConnection();
            databaseConnection.initializeDatabase();

            userDao = new UserDao(databaseConnection);
            accommodationDao = new AccommodationDao(databaseConnection, userDao);
            savedListingDAO = new SavedListingDAO(databaseConnection);

            userService = new UserService(userDao, savedListingDAO);
            accommodationService = new AccommodationService(accommodationDao, userDao);

            // Initialize UI Panels
            this.mainApplicationPanel = new MainApplicationPanel(accommodationService, userService, this);
            this.registrationPanel = new RegistrationPanel(userService, this);
            this.loginPanel = new LoginPanel(userService, this);
            this.contactPanel = new ContactPanel(this);
            this.faqPanel = new FaqPanel(this);

            // Initialise MenuBar
            this.menuBarManager = new MenuBarManager(this);
            JMenuBar menuBar = menuBarManager.createMenuBar();
            this.setJMenuBar(menuBar);


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

            // update menu state after user check
            menuBarManager.updateMenuState(this.currentUser);

            SwingUtilities.invokeLater(() -> {
                if (mainApplicationPanel != null && mainApplicationPanel.getSearchField() != null) {
                    System.out.println("Requesting initial focus for search field.");
                    mainApplicationPanel.getSearchField().requestFocusInWindow();
                } else {
                    System.err.println("Could not set initial focus: main panel or search field is null.");
                }
            });

        } catch (SQLException e) {
            System.err.println("FATAL ERROR during application startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize the application database." +
                            "\nPlease check logs or contact support.\nError: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        } catch (Exception e) {
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

            // Update UI in MainApplicationPanel
            if (mainApplicationPanel != null) {
                mainApplicationPanel.showLoggedInState(currentUser.getUsername());
            }

            if (menuBarManager != null) {
                menuBarManager.updateMenuState(this.currentUser);
            }

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
        saveJwtToPreferences(null);

        // Update the UI in MainApplicationPanel
        if (mainApplicationPanel != null) {
            mainApplicationPanel.showLoggedOutState();
        }

        // Update menu state when logged out
        if (menuBarManager != null) {
            menuBarManager.updateMenuState(null);
        }

        showMainApplicationView();
    }

    private String getJwtFromPreferences() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
        return prefs.get("jwtToken", null);
    }


    public void showUserProfileDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your profile.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserProfileDialog userProfileDialog = new UserProfileDialog(this, userService, currentUser);
        userProfileDialog.setVisible(true);

    }

    public void switchToContactPanel() {
        setContentPane(contactPanel.getContactPanel());
        revalidate();
        repaint();
        System.out.println("Switched to Contact Panel");
    }

    public void switchToFaqPanel() {
        setContentPane(faqPanel.getFaqPanel());
        revalidate();
        repaint();
        System.out.println("Switched to Faq Panel");
    }

    public void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "ResFinder version 1.0\nYour one-stop solution for student accommodation.",
                "About ResFinder",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshMainViewListings() {
        if (mainApplicationPanel != null) {
            System.out.println("Menu: Refreshing listings...");
            mainApplicationPanel.loadInitialListings();
        }
    }

    public void clearMainViewFilters() {
        if (mainApplicationPanel != null) {
            System.out.println("Menu: Clearing filters and search...");
            mainApplicationPanel.clearAllFiltersAndSearch();
        }
    }

    public void switchToRegistrationPanel() {
        setContentPane(registrationPanel.getRegistrationPanel());
        revalidate();
        repaint();
        registrationPanel.requestInitialFocus();
        System.out.println("Switched to registration panel");
    }

    public void switchToLoginPanel() {
        setContentPane(loginPanel.getLoginPanel());
        revalidate();
        repaint();
        loginPanel.requestInitialFocus();
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

    public void showMainApplicationView() {

        if (mainApplicationPanel == null) {
            System.err.println("Error: MainApplicationPanel is null when trying to show it.");
            throw new IllegalStateException("MainApplicationPanel cannot be null when showing the main app view");
        }

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
                accommodationDetailPanel = new AccommodationDetailPanel(accommodationService, this, accommodationId);
                setContentPane(accommodationDetailPanel.getDetailPanel());
                revalidate();
                repaint();
                System.out.println("Successfully switched to detailed view for: " + accommodation.getTitle());
            }
            else {
                System.err.println("Accommodation with ID " + accommodationId + " not found.");
                JOptionPane.showMessageDialog(this,
                        "Could not find details for the selected accommodation.",
                        "Listing Not Found",
                        JOptionPane.WARNING_MESSAGE);
                showMainApplicationView();
            }

            if (mainApplicationPanel != null) {
                if (currentUser != null) mainApplicationPanel.showLoggedInState(currentUser.getUsername());
                else mainApplicationPanel.showLoggedOutState();
            }


        } catch (SQLException e) {
            System.err.println("Database error fetching accommodation details for ID " + accommodationId + ": " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred while retrieving accommodation details.\nPlease try again later.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            showMainApplicationView();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public String getCurrentJwtToken() {
        return currentJwtToken;
    }

    public JFrame getMainFrame() {
        return this;
    }

}