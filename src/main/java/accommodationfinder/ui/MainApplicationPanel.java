package accommodationfinder.ui;

import accommodationfinder.filter.FilterCriteria;
import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
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
    private FilterCriteria currentFilterCriteria = new FilterCriteria();
    private JScrollPane scrollPane;

    // Data Lists
    private List<Accommodation> allFetchedListings = new ArrayList<>();
    private List<Accommodation> currentlyDisplayedListings = new ArrayList<>();

    // Constants for Combo Box
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

    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BUTTON_BACKGROUND_COLOR = new Color(230, 230, 230);


    public MainApplicationPanel(AccommodationService accommodationService, UserService userService,
                                MainWindow mainWindow) {
        this.accommodationService = accommodationService;
        this.userService = userService;
        this.mainWindow = mainWindow;

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);


        // UI Setup
        JPanel topBarPanel = createTopBar();
        mainPanel.add(topBarPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        JLabel mainTitleLabel = new JLabel("Find Student Accommodation to Rent", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainTitleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JPanel searchFilterPanel = createSearchFilterBar();
        JPanel titleAndSearchPanel = new JPanel();
        titleAndSearchPanel.setLayout(new BoxLayout(titleAndSearchPanel, BoxLayout.Y_AXIS));
        titleAndSearchPanel.setOpaque(false);
        titleAndSearchPanel.add(mainTitleLabel);
        titleAndSearchPanel.add(searchFilterPanel);

        centerPanel.add(titleAndSearchPanel, BorderLayout.NORTH);

        listingGridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        listingGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        listingGridPanel.setOpaque(false);

        this.scrollPane = new JScrollPane(listingGridPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(this.scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        loadInitialListings(); // Load data asynchronously
        showLoggedOutState();  // Set initial auth state
    }

    // Data Loading

    private void loadInitialListings() {
        // Display loading message
        listingGridPanel.removeAll();
        listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel loadingLabel = new JLabel("Loading listings... Please wait.");
        loadingLabel.setForeground(TEXT_COLOR);
        listingGridPanel.add(loadingLabel);
        listingGridPanel.revalidate();
        listingGridPanel.repaint();

        SwingWorker<List<Accommodation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Accommodation> doInBackground() throws SQLException {
                // Fetch listings from the service
                return accommodationService.getAllActiveListings();
            }

            @Override
            protected void done() {
                try {
                    allFetchedListings = get(); // Retrieve the fetched list

                    // Initial display no filters (default), default sort, no search
                    updateDisplayedListings();

                } catch (InterruptedException | ExecutionException | CancellationException e) {

                    // Handle errors during fetching or processing
                    Throwable cause = e.getCause();
                    System.err.println("Error loading: " + (cause != null ? cause.getMessage() : e.getMessage()));
                    displayLoadingError("Error loading listings. Please try again later.");

                    // Provide feedback to the user
                    if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(mainPanel, "Database error loading listings.",
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "An unexpected error occurred while loading " +
                                "listings.", "Loading Error", JOptionPane.ERROR_MESSAGE);
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
        worker.execute(); // Start the background worker
    }

    // Helper method to create the Search/Filter Bar
    private JPanel createSearchFilterBar() {
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        searchFilterPanel.setOpaque(false);

        // Search Area
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchPanel.add(searchLabel);
        searchField = new JTextField(30);
        styleTextField(searchField);
        searchField.setToolTipText("Enter keywords and press Enter to search");
        searchField.addActionListener(e -> updateDisplayedListings());
        searchPanel.add(searchField);


        // Order By ComboBox
        JPanel orderByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        orderByPanel.setOpaque(false);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        orderByPanel.add(sortLabel);

        String[] orderByOptions = {
                ORDER_BY_DEFAULT,
                ORDER_BY_PRICE_ASC,
                ORDER_BY_PRICE_DESC,
                ORDER_BY_DATE_OLDEST };

        orderByComboBox = new JComboBox<>(orderByOptions);
        orderByComboBox.setFont(new Font("SansSerif", Font.PLAIN, 12));

        orderByComboBox.addActionListener(e -> updateDisplayedListings());
        orderByPanel.add(orderByComboBox);




        // Filter Button
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setOpaque(false);
        filterButton = new JButton("Filters");
        styleButton(filterButton, BUTTON_BACKGROUND_COLOR, TEXT_COLOR, 13);
        filterButton.setToolTipText("Apply filters to listings");

        filterButton.addActionListener(e -> {
            FilterDialog filterDialog = new FilterDialog(mainWindow, currentFilterCriteria,
                    new ArrayList<>(allFetchedListings));

            filterDialog.setVisible(true);

            if (filterDialog.wereFiltersApplied()) {
                this.currentFilterCriteria = filterDialog.getAppliedCriteria();
                updateDisplayedListings();
            }
        });
        filterPanel.add(filterButton);

        // Add components to the main search/filter panel
        searchFilterPanel.add(searchPanel);
        searchFilterPanel.add(orderByPanel);
        searchFilterPanel.add(filterPanel);

        return searchFilterPanel;
    }


    // Search Implementation

    /**
     * Filters the input list based on the provided search keywords.
     *
     * @param inputList   The list of accommodations to search within.
     * @param rawKeywords The raw text entered by the user in the search field.
     * @return A new list containing only accommodations matching the keywords.
     */
    private List<Accommodation> performSearch(List<Accommodation> inputList, String rawKeywords) {
        String processedKeywords = rawKeywords.trim().toLowerCase();

        // If search is empty, return the original list
        if (processedKeywords.isEmpty()) {
            return new ArrayList<>(inputList); // Return a copy
        }

        // Split search query into individual keywords, handles multiple spaces
        String[] keywords = processedKeywords.split("\\s+");

        List<Accommodation> results = new ArrayList<>();
        for (Accommodation acc : inputList) {
            boolean allKeywordsMatch = true; // Assume match until proven otherwise
            for (String keyword : keywords) {
                if (!accommodationContainsKeyword(acc, keyword)) {
                    allKeywordsMatch = false; // If any keyword doesn't match, reject this accommodation
                    break;
                }
            }
            if (allKeywordsMatch) {
                results.add(acc);
            }
        }
        return results;
    }

    /**
     * Checks if accommodation contains a specific keyword in its searchable fields.
     * Performs case-insensitive checking and handles null values.
     *
     * @param acc     The Accommodation object to check.
     * @param keyword The single keyword (in lowercase) to search for.
     * @return true if the keyword is found in any relevant field, false otherwise.
     */
    private boolean accommodationContainsKeyword(Accommodation acc, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true; // An empty keyword "matches" everything
        }
        if (acc == null) {
            return false;
        }

        // Check Title
        if (acc.getTitle() != null && acc.getTitle().toLowerCase().contains(keyword)) {
            return true;
        }
        // Check Description (null-safe and case-insensitive)
        if (acc.getDescription() != null && acc.getDescription().toLowerCase().contains(keyword)) {
            return true;
        }
        // Check Address (null-safe and case-insensitive)
        if (acc.getAddress() != null && acc.getAddress().toLowerCase().contains(keyword)) {
            return true;
        }
        // Check City (null-safe and case-insensitive)
        if (acc.getCity() != null && acc.getCity().toLowerCase().contains(keyword)) {
            return true;
        }


        return false; // Keyword not found in any specified field
    }

    // Central Update Logic
    /**
     * Central method to apply filtering (future), searching, and sorting,
     * then update the displayed listings grid.
     */
    private void updateDisplayedListings() {
        // Get Current State from UI Controls
        String searchText = searchField.getText();
        String sortCriterion = (String) orderByComboBox.getSelectedItem();

        // Apply Filter -> Search -> Sort
        List<Accommodation> currentList = new ArrayList<>(allFetchedListings);

        // Apply Filtering
        currentList = applyFilters(currentList, currentFilterCriteria);

        // Apply Keyword Search
        currentList = performSearch(currentList, searchText);

        // Apply Sorting
        // Ensure sortCriterion is not null, default if necessary
        if (sortCriterion == null) {
            sortCriterion = ORDER_BY_DEFAULT;
        }
        currentList = AccommodationSorter.sort(currentList, sortCriterion);

        // Update Internal State and Refresh UI
        currentlyDisplayedListings = currentList;
        refreshListingGrid(currentlyDisplayedListings);
    }

    private List<Accommodation> applyFilters(List<Accommodation> inputList, FilterCriteria criteria) {
        if (criteria == null || !criteria.hasActiveFilters()) {
            return new ArrayList<>(inputList);
        }

        List<Accommodation> filteredList = new ArrayList<>();
        for (Accommodation accommodation : inputList) {
            boolean matchesAllCriteria = true;

            // Type Filter
            Set<Accommodation.AccommodationType> selectedTypes = criteria.getSelectedTypes();
            if (selectedTypes != null && !selectedTypes.isEmpty()) {
                if (accommodation.getType() == null || !selectedTypes.contains(accommodation.getType())) {
                    matchesAllCriteria = false;
                }
            }

            // Min Price Filter
            if (matchesAllCriteria && criteria.getMinPrice() != null) {
                if (accommodation.getPrice() == null ||
                        accommodation.getPrice().compareTo(criteria.getMinPrice()) < 0) {
                    matchesAllCriteria = false;
                }
            }

            // Max price Filter
            if (matchesAllCriteria && criteria.getMaxPrice() != null) {
                if (accommodation.getPrice() == null ||
                        accommodation.getPrice().compareTo(criteria.getMaxPrice()) > 0) {
                    matchesAllCriteria = false;
                }
            }

            // Bedrooms filter
            if (matchesAllCriteria && criteria.getBedrooms() != null && criteria.getBedrooms() > 0) {
                if (accommodation.getBedrooms() < criteria.getBedrooms()) {
                    matchesAllCriteria = false;
                }
            }

            // Bathrooms Filter
            if (matchesAllCriteria && criteria.getBathrooms() != null && criteria.getBathrooms() > 0) {
                if (accommodation.getBathrooms() < criteria.getBathrooms()) {
                    matchesAllCriteria = false;
                }
            }

            // City Filter
            if (matchesAllCriteria && criteria.getCity() != null) {
                if (accommodation.getCity() == null || !accommodation.getCity().equalsIgnoreCase(criteria.getCity())) {
                    matchesAllCriteria = false;
                }
            }

            // Utilities Included Filter (true means must be true, false means must be false, null means don't care)
            if (matchesAllCriteria && criteria.getUtilitiesIncluded() != null) {
                if (accommodation.isUtilitiesIncluded() != criteria.getUtilitiesIncluded()) {
                    matchesAllCriteria = false;
                }
            }

            // NSFAS Accredited Filter
            if (matchesAllCriteria && criteria.getNsfasAccredited() != null) {
                if (accommodation.isNsfasAccredited() != criteria.getNsfasAccredited()) {
                    matchesAllCriteria = false;
                }
            }

            if (matchesAllCriteria) {
                filteredList.add(accommodation);
            }
        }
        return filteredList;
    }


    // Helper method to create the Top Bar
    private JPanel createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setOpaque(false);
        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        appTitleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        appTitleLabel.setForeground(TEXT_COLOR);

        authAreaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        authAreaPanel.setOpaque(false);
        topBarPanel.add(appTitleLabel, BorderLayout.WEST);
        topBarPanel.add(authAreaPanel, BorderLayout.EAST);
        return topBarPanel;
    }

    // Method to update UI for Logged In state
    public void showLoggedInState(String username) {
        authAreaPanel.removeAll();
        welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        styleButton(signUpButton, BACKGROUND_COLOR, TEXT_COLOR, 14);
        signUpButton.setPreferredSize(new Dimension(90, 35));

        loginButton = new JButton("Login");
        styleButton(loginButton, BACKGROUND_COLOR, TEXT_COLOR, 14);
        loginButton.setPreferredSize(new Dimension(90, 35));


        signUpButton.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        loginButton.addActionListener(e -> mainWindow.switchToLoginPanel());

        authAreaPanel.add(signUpButton);
        authAreaPanel.add(loginButton);
        authAreaPanel.revalidate();
        authAreaPanel.repaint();
    }



    private void refreshListingGrid(List<Accommodation> listings) {
        listingGridPanel.removeAll();

        if (listings == null || listings.isEmpty()) {
            // Display a message if no listings match criteria
            listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            listingGridPanel.add(new JLabel("No accommodation listings found matching your criteria."));
        } else {
            // Set grid layout and populate with cards
            listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Accommodation acc : listings) {
                AccommodationCardPanel card = new AccommodationCardPanel(acc, mainWindow);
                listingGridPanel.add(card);
            }
        }

        // Reset scroll pane to top AFTER components are  added/removed
        SwingUtilities.invokeLater(() -> {
            if (scrollPane != null && scrollPane.getViewport() != null) {
                scrollPane.getViewport().setViewPosition(new Point(0, 0));
            }
            else { System.err.println("Warning: scrollPane/viewport null during scroll reset."); }
        });

        // Tell the layout manager to recalculate and repaint
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
     * Helper to style JButtons consistently.
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }


    public JTextField getSearchField() {
        return searchField;
    }

    // Method to return the main panel for MainWindow to display
    public JPanel getMainPanel() {
        return mainPanel;
    }
}