package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainApplicationPanel {

    private JPanel mainPanel;

    // Top Bar Components
    private JPanel authAreaPanel;
    private JButton signUpButton;
    private JLabel welcomeLabel;
    private JButton loginButton;
    private JButton logoutButton;

    // Search/Filter Components
    private JTextField searchField;
    private JComboBox<String> orderByComboBox;
    private JButton filterButton;
    private JScrollPane scrollPane;

    private List<Accommodation> allFetchedListings = new ArrayList<>();
    private List<Accommodation> currentlyDisplayedListings = new ArrayList<>();


    public static final String ORDER_BY_DEFAULT = "Default(Newest)";
    public static final String ORDER_BY_PRICE_ASC = "Price: Low to High";
    public static final String ORDER_BY_PRICE_DESC = "Price: High to Low";
    public static final String ORDER_BY_DATE_OLDEST = "Date Listed: Oldest";

    // Listing Area Components
    private JPanel listingGridPanel;

    // References
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;

    public MainApplicationPanel(AccommodationService accommodationService, UserService userService,
                                MainWindow mainWindow) {
        this.accommodationService = accommodationService;
        this.userService = userService;
        this.mainWindow = mainWindow;

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // UI Setup remains the same
        JPanel topBarPanel = createTopBar();
        mainPanel.add(topBarPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JLabel mainTitleLabel = new JLabel("Find Student Accommodation to Rent", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JPanel searchFilterPanel = createSearchFilterBar();
        JPanel titleAndSearchPanel = new JPanel();
        titleAndSearchPanel.setLayout(new BoxLayout(titleAndSearchPanel, BoxLayout.Y_AXIS));
        titleAndSearchPanel.add(mainTitleLabel);
        titleAndSearchPanel.add(searchFilterPanel);
        centerPanel.add(titleAndSearchPanel, BorderLayout.NORTH);
        listingGridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        listingGridPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        this.scrollPane = new JScrollPane(listingGridPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(this.scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);


        loadAndDisplayListings(); // Load data
        showLoggedOutState(); // Set initial auth state
    }

    // Method to Load and Display Listings
    private void loadAndDisplayListings() {
        listingGridPanel.removeAll();
        listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        listingGridPanel.add(new JLabel("Loading listings... Please wait."));
        listingGridPanel.revalidate();
        listingGridPanel.repaint();

        SwingWorker<List<Accommodation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Accommodation> doInBackground() throws SQLException {
                return accommodationService.getAllActiveListings();
            }

            @Override
            protected void done() {
                try {
                    allFetchedListings = get(); // Fetch all

                    // Uses sorter for the initial default sort
                    currentlyDisplayedListings = AccommodationSorter.sort(allFetchedListings, ORDER_BY_DEFAULT);

                    // Refresh the UI with the initially sorted list
                    refreshListingGrid(currentlyDisplayedListings);

                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause();
                    String errorMsg = "Error loading accommodation listings: " + (cause != null ? cause.getMessage()
                            : e.getMessage());
                    System.err.println(errorMsg);
                    e.printStackTrace();
                    displayLoadingError("Error loading listings. Please try again later.");
                    // Show specific dialog based on cause
                    if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(mainPanel, "Database error loading listings.",
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "An unexpected error occurred while " +
                                "loading listings.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    System.err.println("Unexpected error during listing load completion: " + e.getMessage());
                    e.printStackTrace();
                    displayLoadingError("An unexpected error occurred.");
                    JOptionPane.showMessageDialog(mainPanel, "An unexpected error occurred.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Helper method to create the Top Bar
    private JPanel createTopBar() {
        // ... (same as before)
        JPanel topBarPanel = new JPanel(new BorderLayout());
        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        appTitleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        authAreaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authAreaPanel.setOpaque(false);
        topBarPanel.add(appTitleLabel, BorderLayout.WEST);
        topBarPanel.add(authAreaPanel, BorderLayout.EAST);
        return topBarPanel;
    }

    // Method to update UI for Logged In state
    public void showLoggedInState(String username) {
        // ... (same as before)
        authAreaPanel.removeAll();
        welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainWindow.handleLogout());
        authAreaPanel.add(welcomeLabel);
        authAreaPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        authAreaPanel.add(logoutButton);
        authAreaPanel.revalidate();
        authAreaPanel.repaint();
    }

    // Method to update UI for Logged Out state
    public void showLoggedOutState() {
        authAreaPanel.removeAll();
        signUpButton = new JButton("Sign Up");
        loginButton = new JButton("Login");
        signUpButton.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        loginButton.addActionListener(e -> mainWindow.switchToLoginPanel());
        authAreaPanel.add(signUpButton);
        authAreaPanel.add(loginButton);
        authAreaPanel.revalidate();
        authAreaPanel.repaint();
    }


    // Helper method to create the Search/Filter Bar
    private JPanel createSearchFilterBar() {
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Search area:"));
        searchPanel.add(searchField);
        String[] orderByOptions = {
                ORDER_BY_DEFAULT,
                ORDER_BY_PRICE_ASC,
                ORDER_BY_PRICE_DESC,
                ORDER_BY_DATE_OLDEST,
        };

        orderByComboBox = new JComboBox<>(orderByOptions);
        orderByComboBox.addActionListener(e -> applySortingAndRefreshUI());
        JPanel orderByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        orderByPanel.add(new JLabel("Sort by:"));
        orderByPanel.add(orderByComboBox);
        filterButton = new JButton("Filter:");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.add(filterButton);
        searchFilterPanel.add(searchPanel);
        searchFilterPanel.add(orderByPanel);
        searchFilterPanel.add(filterPanel);
        filterButton.addActionListener(e -> System.out.println("Filter button clicked"));
        return searchFilterPanel;
    }

    // refreshListingGrid
    private void refreshListingGrid(List<Accommodation> listings) {
        listingGridPanel.removeAll();
        if (listings == null || listings.isEmpty()) {
            listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            listingGridPanel.add(new JLabel("No accommodation listings found matching your criteria."));
        } else {
            listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Accommodation acc : listings) {
                AccommodationCardPanel card = new AccommodationCardPanel(acc, mainWindow);
                listingGridPanel.add(card);
            }
        }
        SwingUtilities.invokeLater(() -> {
                    if (scrollPane != null && scrollPane.getViewport() != null) {
                        scrollPane.getViewport().setViewPosition(new Point(0, 0));
                    } else {
                        System.err.println("Warning: scrollPane or its viewport was null during " +
                                "refreshListingGrid scroll reset.");
                    }
                }
        );
        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }

    // displayLoadingError
    private void displayLoadingError(String message) {
        listingGridPanel.removeAll();
        listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        listingGridPanel.add(errorLabel);
        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }

    /**
     * Applies sorting using AccommodationSorter based on the combo box selection
     * and refreshes the UI.
     */
    private void applySortingAndRefreshUI() {
        String selectedOrder = (String) orderByComboBox.getSelectedItem();
        if (selectedOrder == null) {
            return;
        }


        // Update the list that the UI is currently displaying
        currentlyDisplayedListings = AccommodationSorter.sort(allFetchedListings, selectedOrder);

        refreshListingGrid(currentlyDisplayedListings);
    }

    // Method to return the main panel for MainWindow to display
    public JPanel getMainPanel() {
        return mainPanel;
    }
}