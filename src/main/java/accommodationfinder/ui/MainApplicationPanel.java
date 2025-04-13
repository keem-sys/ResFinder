package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation; // Import Accommodation class
import accommodationfinder.service.AccommodationService;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    // Formatters (create once)
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA")); // ZAR formatting
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy"); // Example date format

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
        // Clear existing listings (important if this method is called again later, e.g., after filtering)
        listingGridPanel.removeAll();

        try {
            // Fetch data from the service
            List<Accommodation> listings = accommodationService.getAllActiveListings(); // Assuming this method exists

            if (listings.isEmpty()) {
                listingGridPanel.setLayout(new FlowLayout()); // Change layout for message
                listingGridPanel.add(new JLabel("No accommodation listings found."));
            } else {
                listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
                for (Accommodation acc : listings) {
                    listingGridPanel.add(createListingCard(acc));
                }
            }

        } catch (SQLException e) {
            // Handle database errors
            System.err.println("Error loading accommodation listings: " + e.getMessage());
            e.printStackTrace(); // Log the full error for debugging
            listingGridPanel.setLayout(new FlowLayout()); // Change layout for error message
            listingGridPanel.add(new JLabel("Error loading listings. Please try again later."));
            JOptionPane.showMessageDialog(mainPanel, "Error loading listings.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Refresh the panel layout
        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }


    // --- Helper method to create a single listing card from REAL data ---
    private JPanel createListingCard(Accommodation accommodation) {
        JPanel cardPanel = new JPanel(new BorderLayout(5, 5));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cardPanel.setBackground(Color.WHITE);

        // --- Image Panel ---
        // Use a standard size for consistency
        final int IMG_WIDTH = 150;
        final int IMG_HEIGHT = 120;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout for centering
        imagePanel.setBackground(new Color(220, 220, 220)); // Placeholder background
        imagePanel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));

        JLabel imageLabel = new JLabel("Loading..."); // Initial text
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT)); // Size the label itself
        imagePanel.add(imageLabel); // Add label to the panel

        // --- Load Image in Background ---
        // Check if there are image URLs and the first one is not null/empty
        if (accommodation.getImageUrls() != null && !accommodation.getImageUrls().isEmpty() &&
                accommodation.getImageUrls().get(0) != null && !accommodation.getImageUrls().get(0).trim().isEmpty()) {

            String imageUrlString = accommodation.getImageUrls().get(0).trim(); // Get the first URL

            // Use SwingWorker to load the image off the Event Dispatch Thread (EDT)
            SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    try {
                        // IMPORTANT: imageUrlString MUST be a DIRECT link to the image data
                        URL imageUrl = new URL(imageUrlString);
                        ImageIcon originalIcon = new ImageIcon(imageUrl);

                        // Check if loading failed
                        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                            System.err.println("Failed to load image: " + imageUrlString + " (Status: " + originalIcon.getImageLoadStatus()+")");
                            return null; // Indicate failure
                        }

                        // Scale the image
                        Image image = originalIcon.getImage();
                        Image scaledImage = image.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);

                    } catch (MalformedURLException e) {
                        System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                        return null; // Indicate failure
                    } catch (Exception e) {
                        // Catch other potential errors during loading/scaling
                        System.err.println("Error loading/scaling image: " + imageUrlString + " - " + e.getMessage());
                        e.printStackTrace(); // Log the full error
                        return null; // Indicate failure
                    }
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon scaledIcon = get();
                        if (scaledIcon != null) {
                            imageLabel.setIcon(scaledIcon);
                            imageLabel.setText(null);
                        } else {
                            // Loading failed or returned null
                            imageLabel.setText("No Image"); // Or "Error"
                            imageLabel.setIcon(null);
                        }
                    } catch (Exception e) {
                        // Handle exceptions from get() or during UI update
                        System.err.println("Error updating image label: " + e.getMessage());
                        imageLabel.setText("Error");
                        imageLabel.setIcon(null);
                    }
                    // Ensure layout is updated after potential size changes
                    imagePanel.revalidate();
                    imagePanel.repaint();
                }
            };
            imageLoader.execute();
        } else {
            // No image URL provided
            imageLabel.setText("No Image");
            imageLabel.setIcon(null);
        }

        cardPanel.add(imagePanel, BorderLayout.NORTH); // Image panel at the top


        // --- Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(5, 8, 5, 8));
        detailsPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(limitString(accommodation.getTitle(), 30));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(currencyFormatter.format(accommodation.getPrice()) + " " + formatPriceFrequency(accommodation.getPriceFrequency()));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        priceLabel.setForeground(new Color(0, 100, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(String.format("%d Bed | %d Bath | %s",
                accommodation.getBedrooms(),
                accommodation.getBathrooms(),
                accommodation.getCity()));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nsfasLabel = new JLabel("NSFAS: " + (accommodation.isNsfasAccredited() ? "Yes" : "No"));
        nsfasLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        nsfasLabel.setForeground(accommodation.isNsfasAccredited() ? Color.BLUE : Color.GRAY);
        nsfasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(infoLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(nsfasLabel);

        cardPanel.add(detailsPanel, BorderLayout.CENTER);

        // --- Availability Label
        String availableText = "Available: ";
        if (accommodation.getAvailableFrom() != null) {
            if (accommodation.getAvailableFrom().isAfter(LocalDateTime.now())) {
                availableText += accommodation.getAvailableFrom().format(dateFormatter);
            } else {
                availableText += "Now";
            }
        } else {
            availableText += "Contact Lister";
        }
        JLabel availableLabel = new JLabel(availableText);
        availableLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        availableLabel.setBorder(new EmptyBorder(3, 8, 5, 8));
        availableLabel.setOpaque(true);
        availableLabel.setBackground(new Color(240, 240, 240));
        cardPanel.add(availableLabel, BorderLayout.SOUTH);


        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("Listing card clicked! ID: " + accommodation.getId());
                JOptionPane.showMessageDialog(mainWindow, "Details for: " + accommodation.getTitle(), "Listing Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return cardPanel;
    }

    // Helper to format PriceFrequency enum nicely
    private String formatPriceFrequency(Accommodation.PriceFrequency frequency) {
        if (frequency == null) return "";
        return switch (frequency) {
            case PER_MONTH -> "/ month";
            case PER_WEEK -> "/ week";
            case PER_SEMESTER -> "/ semester";
            case PER_NIGHT -> "/ night";
            case OTHER -> "";
        };
    }

    // Helper to limit string length for display
    private String limitString(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength - 3) + "...";
        }
    }


    // Helper method to create the Top Bar (No changes needed from previous version)
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

    // Helper method to create the Search/Filter Bar (No changes needed from previous version)
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