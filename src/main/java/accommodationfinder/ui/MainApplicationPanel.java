package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation; // Import Accommodation class
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException; // Import SQLException
import java.text.NumberFormat; // For currency formatting
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // For date formatting
import java.util.List; // Import List
import java.util.Locale; // For currency formatting


public class MainApplicationPanel {

    private JPanel mainPanel; // The main container panel for this view

    // Top Bar Components
    private JButton signUpButton;
    private JButton loginButton;

    // Search/Filter Components
    private JTextField searchField;
    private JComboBox<String> orderByComboBox;
    private JButton filterButton;

    // Listing Area Components
    private JPanel listingGridPanel; // Panel to hold the grid of listings

    // References
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;


    public MainApplicationPanel(AccommodationService accommodationService, UserService userService, MainWindow mainWindow) { // Corrected order to match MainWindow instantiation
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

        // --- Title and Search/Filter ---
        JLabel mainTitleLabel = new JLabel("Find Student Accommodation to Rent", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JPanel searchFilterPanel = createSearchFilterBar();
        JPanel titleAndSearchPanel = new JPanel();
        titleAndSearchPanel.setLayout(new BoxLayout(titleAndSearchPanel, BoxLayout.Y_AXIS));
        titleAndSearchPanel.add(mainTitleLabel);
        titleAndSearchPanel.add(searchFilterPanel);
        centerPanel.add(titleAndSearchPanel, BorderLayout.NORTH);
        // --- End Title and Search/Filter ---


        // --- Listing Area ---
        // Initialize the grid panel first
        // Using GridLayout: 0 rows means flexible, 2 columns, 15px hgap, 15px vgap
        listingGridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        listingGridPanel.setBorder(new EmptyBorder(15, 0, 0, 0)); // Padding above the grid

        // Wrap in ScrollPane
        JScrollPane scrollPane = new JScrollPane(listingGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Improve scroll speed

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        // --- End Listing Area ---

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Load data AFTER UI components are initialized ---
        loadAndDisplayListings();
    }

    // --- Method to Load and Display Listings ---
    private void loadAndDisplayListings() {
        listingGridPanel.removeAll();

        try {
            List<Accommodation> listings = accommodationService.getAllActiveListings();

            if (listings.isEmpty()) {
                listingGridPanel.setLayout(new FlowLayout());
                listingGridPanel.add(new JLabel("No accommodation listings found."));
            } else {
                // Reset layout to Grid
                listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
                for (Accommodation acc : listings) {
                    // *** Use the new AccommodationCardPanel ***
                    AccommodationCardPanel card = new AccommodationCardPanel(acc, mainWindow);
                    listingGridPanel.add(card);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading accommodation listings: " + e.getMessage());
            e.printStackTrace();
            listingGridPanel.setLayout(new FlowLayout());
            listingGridPanel.add(new JLabel("Error loading listings. Please try again later."));
            JOptionPane.showMessageDialog(mainPanel, "Error loading listings.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }

    // Helper method to create the Top Bar
    private JPanel createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        appTitleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        signUpButton = new JButton("Sign Up");
        loginButton = new JButton("Login");

        // Add Action Listeners
        signUpButton.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        loginButton.addActionListener(e -> mainWindow.switchToLoginPanel());

        JPanel authButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authButtonsPanel.add(signUpButton);
        authButtonsPanel.add(loginButton);

        topBarPanel.add(appTitleLabel, BorderLayout.WEST);
        topBarPanel.add(authButtonsPanel, BorderLayout.EAST);
        return topBarPanel;
    }

    // Helper method to create the Search/Filter Bar
    private JPanel createSearchFilterBar() {
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        // ... (rest of search/filter bar code is the same) ...
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