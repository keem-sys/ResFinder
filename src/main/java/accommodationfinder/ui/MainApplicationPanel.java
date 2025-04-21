package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;


public class MainApplicationPanel {

    private JPanel mainPanel;

    // Top Bar Components
    private JPanel authAreaPanel; // Panel to hold dynamic login/logout component
    private JButton signUpButton;
    private JLabel welcomeLabel;
    private JButton loginButton;
    private JButton logoutButton;

    // Search/Filter Components
    private JTextField searchField;
    private JComboBox<String> orderByComboBox;
    private JButton filterButton;

    // Listing Area Components
    private JPanel listingGridPanel;

    // References
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;



    public MainApplicationPanel(AccommodationService accommodationService, UserService userService, MainWindow mainWindow) {
        this.accommodationService = accommodationService;
        this.userService = userService;
        this.mainWindow = mainWindow;

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create Top Bar
        JPanel topBarPanel = createTopBar();
        mainPanel.add(topBarPanel, BorderLayout.NORTH);

        // Create Center Content Area
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Title and Search/Filter
        JLabel mainTitleLabel = new JLabel("Find Student Accommodation to Rent", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JPanel searchFilterPanel = createSearchFilterBar();
        JPanel titleAndSearchPanel = new JPanel();
        titleAndSearchPanel.setLayout(new BoxLayout(titleAndSearchPanel, BoxLayout.Y_AXIS));
        titleAndSearchPanel.add(mainTitleLabel);
        titleAndSearchPanel.add(searchFilterPanel);
        centerPanel.add(titleAndSearchPanel, BorderLayout.NORTH);


        // Listing Area
        listingGridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        listingGridPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Wrap in ScrollPane
        JScrollPane scrollPane = new JScrollPane(listingGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        loadAndDisplayListings();
        // Set initial state of the auth area (logged out by default)
        showLoggedOutState();
    }

    // Method to Load and Display Listings
    private void loadAndDisplayListings() {
        listingGridPanel.removeAll();

        try {
            List<Accommodation> listings = accommodationService.getAllActiveListings();

            if (listings.isEmpty()) {
                listingGridPanel.setLayout(new FlowLayout());
                listingGridPanel.add(new JLabel("Sorry for the inconvenience, No accommodation listings found."));
            } else {
                listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
                for (Accommodation acc : listings) {
                    AccommodationCardPanel card = new AccommodationCardPanel(acc, mainWindow);
                    listingGridPanel.add(card);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading accommodation listings: " + e.getMessage());
            e.printStackTrace();
            listingGridPanel.setLayout(new FlowLayout());
            listingGridPanel.add(new JLabel("Error loading listings. Please try again later."));
            JOptionPane.showMessageDialog(mainPanel, "Error loading listings.", "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }

    // Helper method to create the Top Bar
    private JPanel createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        appTitleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Panel that will hold Sign Up/Login OR Welcome/Logout
        authAreaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authAreaPanel.setOpaque(false);

        topBarPanel.add(appTitleLabel, BorderLayout.WEST);
        topBarPanel.add(authAreaPanel, BorderLayout.EAST);


        return topBarPanel;
    }

    // Method to update UI for Logged In state
    public void showLoggedInState(String username) {
        authAreaPanel.removeAll(); // Clear previous components

        welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainWindow.handleLogout()); // Call logout method in MainWindow

        // Optional: Add profile icon/button later here

        authAreaPanel.add(welcomeLabel);
        authAreaPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer
        authAreaPanel.add(logoutButton);

        authAreaPanel.revalidate();
        authAreaPanel.repaint();
    }

    // Method to update UI for Logged Out state
    public void showLoggedOutState() {
        authAreaPanel.removeAll(); // Clear previous components

        signUpButton = new JButton("Sign Up");
        loginButton = new JButton("Login");

        // Add Action Listeners
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
        // Search Area
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Search area:"));
        searchPanel.add(searchField);

        // Order By
        String[] orderByOptions = {"Default", "Price: Low to High", "Price: High to Low", "Date Listed: Newest"};
        orderByComboBox = new JComboBox<>(orderByOptions);
        JPanel orderByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        orderByPanel.add(new JLabel("Order by:"));
        orderByPanel.add(orderByComboBox);

        // Filter
        filterButton = new JButton("Filter:");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.add(filterButton);

        // Add components to the main search/filter panel
        searchFilterPanel.add(searchPanel);
        searchFilterPanel.add(orderByPanel);
        searchFilterPanel.add(filterPanel);

        // Add action listeners for search, order, filter later
        filterButton.addActionListener(e -> System.out.println("Filter button clicked"));
        return searchFilterPanel;
    }

    // Method to return the main panel for MainWindow to display
    public JPanel getMainPanel() {
        return mainPanel;
    }
}