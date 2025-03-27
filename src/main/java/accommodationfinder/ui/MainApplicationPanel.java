package accommodationfinder.ui;

import accommodationfinder.auth.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Import for padding
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JPanel listingGridPanel;

    // References
    private final UserService userService;
    private final MainWindow mainWindow;

    public MainApplicationPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create Top Bar
        JPanel topBarPanel = createTopBar();
        mainPanel.add(topBarPanel, BorderLayout.NORTH);

        // Create Center Content Area (Title, Search/Filter, Listings)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Main Title
        JLabel mainTitleLabel = new JLabel("Find Student Accommodation to Rent", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Search/Filter Bar
        JPanel searchFilterPanel = createSearchFilterBar();

        // Combine Title and Search/Filter
        JPanel titleAndSearchPanel = new JPanel();
        titleAndSearchPanel.setLayout(new BoxLayout(titleAndSearchPanel, BoxLayout.Y_AXIS));
        titleAndSearchPanel.add(mainTitleLabel);
        titleAndSearchPanel.add(searchFilterPanel);

        centerPanel.add(titleAndSearchPanel, BorderLayout.NORTH);

        // Listing Area
        listingGridPanel = createListingGrid();
        JScrollPane scrollPane = new JScrollPane(listingGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

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

    // Helper method to create the Listing Grid (with placeholders)
    private JPanel createListingGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        gridPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // TODO: Replace placeholder with actual data
        for (int i = 0; i < 6; i++) { // Create 6 placeholders as in the wireframe
            gridPanel.add(createListingCardPlaceholder());
        }

        return gridPanel;
    }

    // Helper method to create a single placeholder listing card
    private JPanel createListingCardPlaceholder() {
        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Placeholder for the image
        JPanel imagePlaceholder = new JPanel();
        imagePlaceholder.setBackground(Color.LIGHT_GRAY);
        imagePlaceholder.setPreferredSize(new Dimension(150, 120));

        // Placeholder for availability info
        JLabel availableLabel = new JLabel("Available: ..........");
        availableLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        availableLabel.setOpaque(true);
        availableLabel.setBackground(Color.WHITE);

        cardPanel.add(imagePlaceholder, BorderLayout.CENTER);
        cardPanel.add(availableLabel, BorderLayout.SOUTH);

        // Add mouse listener later for clicking on a card
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("Listing card clicked!");
                // TODO: Show detailed view for this listing
            }
        });


        return cardPanel;
    }


    // Method to return the main panel for MainWindow to display
    public JPanel getMainPanel() {
        return mainPanel;
    }

}