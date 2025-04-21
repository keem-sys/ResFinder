package accommodationfinder.ui;

import accommodationfinder.auth.User; // Import User class
import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder; // For section borders
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Displays the detailed view of a single accommodation listing.
 */
public class AccommodationDetailPanel {

    private JPanel mainPanel;
    private final Accommodation accommodation;
    private final MainWindow mainWindow;
    private final AccommodationService accommodationService;

    // --- UI Components ---
    // ... (Keep component declarations as before) ...
    private JButton backButton;
    private JButton signUpButton;
    private JButton loginButton;
    private JTextField searchField;
    private JLabel titleLabel; // Will use HTML now
    private JPanel imagePanel; // Panel containing the image label
    private JLabel imageLabel;
    private JLabel priceLabel;
    private JLabel addressLabel; // Will use HTML now
    private JLabel bedsBathsLabel;
    private JLabel nsfasLabel;
    private JLabel typeLabel;
    private JLabel occupancyLabel;
    private JLabel availabilityLabel;
    private JLabel leaseTermLabel;
    private JTextArea descriptionTextArea;
    private JPanel amenitiesPanel;
    private JLabel contactTitleLabel;
    private JLabel listedByLabel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton sendMessageButton;


    // Formatting Helpers
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // Colors - Make section background slightly different
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color SECTION_BG_COLOR = new Color(248, 248, 248); // Slightly distinct
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // Lighter border


    public AccommodationDetailPanel(Accommodation accommodation, AccommodationService accommodationService, MainWindow mainWindow) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        this.accommodation = accommodation;
        this.mainWindow = mainWindow;
        this.accommodationService = accommodationService;
        initComponents();
        populateData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10)); // Reduced VGap slightly
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        mainPanel.add(createTopBar(), BorderLayout.NORTH);

        JPanel centerContent = createCenterContent();
        JScrollPane scrollPane = new JScrollPane(centerContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // scrollPane.setBackground(BACKGROUND_COLOR); // Let viewport handle background
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR); // Set viewport background


        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // populateData(); // Moved population call to constructor end
    }

    // --- Top Bar --- (No changes needed)
    private JPanel createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout(10, 0));
        topBarPanel.setBackground(BACKGROUND_COLOR);
        topBarPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Bottom margin

        // Left side: Back Button and Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        backButton = new JButton("<- Back");
        backButton.setToolTipText("Back to Listings");
        backButton.setFocusPainted(false); // Improve look
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());
        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        leftPanel.add(backButton);
        leftPanel.add(appTitleLabel);

        // Right side: Auth Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        signUpButton = new JButton("Sign Up");
        loginButton = new JButton("Login");
        signUpButton.addActionListener(e -> mainWindow.switchToRegistrationPanel());
        loginButton.addActionListener(e -> mainWindow.switchToLoginPanel());
        rightPanel.add(signUpButton);
        rightPanel.add(loginButton);

        topBarPanel.add(leftPanel, BorderLayout.WEST);
        topBarPanel.add(rightPanel, BorderLayout.EAST);
        return topBarPanel;
    }

    // --- Center Content (Search + Main Area) --- (No changes needed)
    private JPanel createCenterContent() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15)); // Vertical gap
        centerPanel.setOpaque(false);

        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(createMainDetailArea(), BorderLayout.CENTER);

        return centerPanel;
    }

    // --- Search Panel --- (No changes needed)
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(new Color(235, 235, 235)); // Lighter gray
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR), // Use lighter border
                new EmptyBorder(5, 10, 5, 10)
        ));
        searchField = new JTextField("Search listings (not implemented)"); // Placeholder text
        searchField.setForeground(Color.GRAY);
        searchField.setEditable(false); // Make non-editable for now

        JButton searchButton = new JButton("Search");
        searchButton.setEnabled(false); // Disabled for now

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        return searchPanel;
    }

    // --- Main Detail Area (Left/Right Split) --- (No changes needed)
    private JPanel createMainDetailArea() {
        JPanel mainAreaPanel = new JPanel(new GridBagLayout());
        mainAreaPanel.setOpaque(false); // Transparent, mainPanel provides background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Fill space allocated
        gbc.insets = new Insets(0, 0, 0, 15); // Right margin for left panel

        // Left Column (Details)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65; // Keep weights
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainAreaPanel.add(createLeftPanel(), gbc);

        // Right Column (Contact)
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // No margin for right panel
        gbc.weightx = 0.35;
        gbc.fill = GridBagConstraints.VERTICAL; // Only fill vertically needed space
        gbc.anchor = GridBagConstraints.NORTHEAST; // Anchor top-right
        mainAreaPanel.add(createRightPanel(), gbc);

        return mainAreaPanel;
    }


    // --- Left Panel (Listing Details) ---
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // 1. Title (Use HTML for wrapping)
        titleLabel = new JLabel("<html>Listing Title Placeholder</html>"); // Use HTML
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Slightly smaller title
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0)); // Adjust spacing
        leftPanel.add(titleLabel);

        // 2. Primary Info Panel
        leftPanel.add(createPrimaryInfoPanel());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adjust spacing

        // 3. Image Panel (Container for the label)
        imagePanel = createImagePanel(); // Assign to field
        leftPanel.add(imagePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adjust spacing

        // 4. Features Panel
        leftPanel.add(createFeaturesPanel());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adjust spacing

        // 5. Amenities Panel
        leftPanel.add(createAmenitiesPanel());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adjust spacing

        // 6. Description Panel
        leftPanel.add(createDescriptionPanel());

        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    // --- Helper for Primary Info Section ---
    private JPanel createPrimaryInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Set Maximum Size to prevent horizontal stretching beyond GridBag allocation
        infoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100)); // Height arbitrary, width is key

        priceLabel = createInfoLabel();
        addressLabel = createInfoLabel(); // Will use HTML
        bedsBathsLabel = createInfoLabel();
        nsfasLabel = createInfoLabel();

        priceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Slightly smaller
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        bedsBathsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nsfasLabel.setFont(new Font("Arial", Font.PLAIN, 13));


        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(addressLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(bedsBathsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(nsfasLabel);

        return infoPanel;
    }

    // --- Helper for Image Section ---
    private JPanel createImagePanel() {
        JPanel imgContainer = new JPanel(new BorderLayout());
        imgContainer.setBackground(Color.DARK_GRAY); // Background while loading
        imgContainer.setBorder(new LineBorder(BORDER_COLOR));
        imgContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Set a reasonable maximum size, the image scaling will handle the rest
        imgContainer.setMaximumSize(new Dimension(Short.MAX_VALUE, 400)); // Limit height
        imgContainer.setMinimumSize(new Dimension(200, 150)); // Prevent collapsing too small


        imageLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        // Don't set preferred size here, let scaling handle it
        imageLabel.setOpaque(true); // Make background visible
        imageLabel.setBackground(Color.LIGHT_GRAY);
        imageLabel.setForeground(Color.BLACK);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER); // Center placeholder text vertically

        imgContainer.add(imageLabel, BorderLayout.CENTER);
        return imgContainer;
    }

    // --- Helper for Titled Border Sections (Features, Amenities, Description) ---
    private TitledBorder createSectionBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR), // Use lighter border
                " " + title + " ", TitledBorder.LEFT, TitledBorder.TOP, // Add spaces around title
                new Font("Arial", Font.BOLD, 13), // Slightly smaller title font
                Color.DARK_GRAY);
    }

    private void styleSectionPanel(JPanel panel) {
        panel.setBackground(SECTION_BG_COLOR);
        panel.setOpaque(true); // Ensure background color is shown
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Constrain width by setting MaximumSize - height can be preferred size
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, panel.getPreferredSize().height));
        // Add internal padding
        panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(), // Keep the TitledBorder
                new EmptyBorder(5, 8, 8, 8) // Add internal padding (top, left, bottom, right)
        ));
    }


    // --- Helper for Features Section ---
    private JPanel createFeaturesPanel() {
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBorder(createSectionBorder("Features")); // Use helper
        styleSectionPanel(featuresPanel); // Apply common styling


        typeLabel = createInfoLabel("Type: ");
        occupancyLabel = createInfoLabel("Max Occupancy: ");
        availabilityLabel = createInfoLabel("Available: ");
        leaseTermLabel = createInfoLabel("Lease Term: ");

        featuresPanel.add(typeLabel);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        featuresPanel.add(occupancyLabel);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        featuresPanel.add(availabilityLabel);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        featuresPanel.add(leaseTermLabel);

        // Re-set maximum height after adding components
        featuresPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, featuresPanel.getPreferredSize().height));

        return featuresPanel;
    }

    // --- Helper for Amenities Section ---
    private JPanel createAmenitiesPanel() {
        amenitiesPanel = new JPanel(new GridLayout(0, 2, 10, 5)); // Grid layout for amenities
        amenitiesPanel.setBorder(createSectionBorder("Amenities")); // Use helper
        styleSectionPanel(amenitiesPanel); // Apply common styling
        amenitiesPanel.setOpaque(true); // Needs opaque for grid layout background

        // Re-set maximum height after potential content changes
        amenitiesPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, amenitiesPanel.getPreferredSize().height));
        return amenitiesPanel;
    }

    // --- Helper for Description Section ---
    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(createSectionBorder("Description")); // Use helper
        styleSectionPanel(descriptionPanel); // Apply common styling

        descriptionTextArea = new JTextArea("Loading description...");
        descriptionTextArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setOpaque(false); // Show panel background

        // *** Apply padding directly to the JTextArea ***
        descriptionTextArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // Add padding here

        JScrollPane scrollPane = new JScrollPane(descriptionTextArea);
        scrollPane.setBorder(null); // Remove scrollpane border (this is fine)
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // *** Remove the illegal line that sets border on viewport ***
        // scrollPane.getViewport().setBorder(new EmptyBorder(0, 0, 0, 0)); // REMOVE THIS LINE

        scrollPane.setPreferredSize(new Dimension(400, 120)); // Adjust preferred height

        descriptionPanel.add(scrollPane, BorderLayout.CENTER);

        // Re-set maximum height
        descriptionPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, descriptionPanel.getPreferredSize().height));

        return descriptionPanel;
    }


    // --- Right Panel (Contact Form / Lister Info) ---
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR), // Use lighter border
                new EmptyBorder(15, 15, 15, 15))
        );
        // Remove setAlignmentY - let GridBag handle vertical alignment
        // rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        contactTitleLabel = new JLabel("Contact Lister");
        contactTitleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Slightly smaller
        contactTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactTitleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        listedByLabel = createInfoLabel("Listed by: ");
        listedByLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        listedByLabel.setBorder(new EmptyBorder(0,0,12,0));

        JPanel namePanel = createFieldPanel(new JLabel("Your Name:"), nameField = new JTextField());
        JPanel emailPanel = createFieldPanel(new JLabel("Your Email:"), emailField = new JTextField());
        JPanel phonePanel = createFieldPanel(new JLabel("Your Phone:"), phoneField = new JTextField());
        styleContactField(nameField);
        styleContactField(emailField);
        styleContactField(phoneField);


        sendMessageButton = new JButton("Send Message");
        sendMessageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleButton(sendMessageButton);
        sendMessageButton.addActionListener(e -> handleSendMessage());


        rightPanel.add(contactTitleLabel);
        rightPanel.add(listedByLabel);
        rightPanel.add(namePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(emailPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(phonePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(sendMessageButton);
        rightPanel.add(Box.createVerticalGlue()); // Pushes content up

        // Set preferred width, let height be determined by content
        rightPanel.setPreferredSize(new Dimension(280, 0)); // Request width, height 0 means preferred
        rightPanel.setMaximumSize(new Dimension(320, Short.MAX_VALUE)); // Allow height growth, limit width


        return rightPanel;
    }

    // Helper to create label + field row for contact form (no changes)
    private JPanel createFieldPanel(JLabel label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false); // Transparent background
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, BorderLayout.NORTH); // Label above field
        panel.add(field, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Limit height
        return panel;
    }

    // Helper to style contact form fields (no changes)
    private void styleContactField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(4, 4, 4, 4)
        ));
    }

    // Helper to style buttons consistently (no changes)
    private void styleButton(JButton button) {
        Dimension buttonSize = new Dimension(150, 30);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize); // Prevent stretching
        button.setFocusPainted(false);
    }

    // Helper to create standard info labels (slightly smaller font)
    private JLabel createInfoLabel() {
        return createInfoLabel("");
    }
    private JLabel createInfoLabel(String prefix) {
        JLabel label = new JLabel(prefix + "..."); // Default text
        label.setFont(new Font("Arial", Font.PLAIN, 13)); // Slightly smaller default
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Set maximum size to allow wrapping if HTML is used, or just prevent excessive width
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, label.getPreferredSize().height));
        return label;
    }


    // --- Data Population ---
    private void populateData() {
        // Use HTML for title and address to allow wrapping
        // Estimate a width for the wrapping (adjust based on layout weight)
        int approxLeftPanelWidth = 550; // Estimate based on 65% weight and typical window size
        titleLabel.setText("<html><body style='width: " + (approxLeftPanelWidth - 20) + "px'>" + accommodation.getTitle() + "</body></html>");
        priceLabel.setText(formatPrice(accommodation.getPrice(), accommodation.getPriceFrequency()));
        addressLabel.setText("<html><body style='width: " + (approxLeftPanelWidth - 20) + "px'>" + formatAddress(accommodation.getAddress(), accommodation.getCity()) + "</body></html>");

        // Other simple labels
        bedsBathsLabel.setText(String.format("%d Beds | %d Baths", accommodation.getBedrooms(), accommodation.getBathrooms()));
        nsfasLabel.setText("NSFAS Accredited: " + formatBoolean(accommodation.isNsfasAccredited()));
        nsfasLabel.setForeground(accommodation.isNsfasAccredited() ? new Color(0, 100, 50) : Color.DARK_GRAY);

        typeLabel.setText("Type: " + (accommodation.getType() != null ? accommodation.getType().name().replace("_", " ") : "N/A")); // Replace underscore
        occupancyLabel.setText("Max Occupancy: " + (accommodation.getMaxOccupancy() > 0 ? accommodation.getMaxOccupancy() + " person(s)" : "N/A"));
        availabilityLabel.setText("Available: " + formatAvailability(accommodation.getAvailableFrom()));
        leaseTermLabel.setText("Lease Term: " + (accommodation.getLeaseTerm() != null && !accommodation.getLeaseTerm().isEmpty() ? accommodation.getLeaseTerm() : "Not specified"));

        // Amenities
        amenitiesPanel.removeAll(); // Clear previous if any
        addAmenityLabel("Internet Included", accommodation.isInternetIncluded());
        addAmenityLabel("Utilities Included", accommodation.isUtilitiesIncluded());
        addAmenityLabel("Parking Available", accommodation.isParkingAvailable());
        if (amenitiesPanel.getComponentCount() == 0) {
            amenitiesPanel.add(new JLabel("No specific amenities listed.")); // Handle case with no boolean amenities
        }
        // Crucial: Update max size *after* adding components to prevent layout issues
        amenitiesPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, amenitiesPanel.getPreferredSize().height));
        amenitiesPanel.revalidate();
        amenitiesPanel.repaint();


        // Description
        descriptionTextArea.setText(accommodation.getDescription() != null && !accommodation.getDescription().trim().isEmpty() ? accommodation.getDescription() : "No description provided.");
        descriptionTextArea.setCaretPosition(0);

        // Image - Pass the container panel for width calculation
        loadImageAsync(imageLabel, imagePanel);

        // Contact Info / Lister
        User lister = accommodation.getListedBy();
        if (lister != null) {
            // Check if getFullName is available, otherwise use getFirstName
            String listerName = lister.getFullName();
            listedByLabel.setText("Listed by: " + lister.getUsername() + (listerName != null ? " ("+listerName+")":""));
        } else {
            listedByLabel.setText("Listed by: Unknown User");
        }

        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    private void addAmenityLabel(String name, boolean isAvailable) {
        if (isAvailable) { // Only show amenities that are true/available? Or show Yes/No? Showing Yes/No as before.
            JLabel amenityLabel = new JLabel(name + ": " + formatBoolean(isAvailable));
            amenityLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // Smaller font for amenities
            amenitiesPanel.add(amenityLabel);
        } else {
            // Optionally add amenities marked as "No" or just omit them
            JLabel amenityLabel = new JLabel(name + ": " + formatBoolean(isAvailable));
            amenityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            amenityLabel.setForeground(Color.GRAY); // Dim unavailable amenities
            amenitiesPanel.add(amenityLabel);
        }
    }

    // --- Formatting Helpers --- (No changes needed in formatPrice, formatAddress, etc.)
    private String formatPrice(java.math.BigDecimal price, Accommodation.PriceFrequency freq) {
        String formattedPrice = (price != null) ? currencyFormatter.format(price) : "N/A";
        String frequencyStr = "";
        if (freq != null) {
            frequencyStr = switch (freq) {
                case PER_MONTH -> "/ Month";
                case PER_WEEK -> "/ Week";
                case PER_SEMESTER -> "/ Semester";
                case PER_NIGHT -> "/ Night";
                case OTHER -> "";
            };
        }
        return formattedPrice + frequencyStr;
    }

    private String formatAddress(String street, String city) {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            sb.append(street);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        return sb.length() > 0 ? sb.toString() : "Address not specified";
    }

    private String formatAvailability(LocalDateTime availableFrom) {
        if (availableFrom == null) {
            return "Contact Lister";
        }
        if (availableFrom.isAfter(LocalDateTime.now())) {
            return "From " + availableFrom.format(dateFormatter);
        } else {
            return "Now";
        }
    }

    private String formatBoolean(boolean value) {
        return value ? "Yes" : "No";
    }


    // --- Action Handlers --- (No changes needed)
    private void handleSendMessage() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Please enter your name and email.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Send Message Action:");
        System.out.println("  From Name: " + name);
        System.out.println("  From Email: " + email);
        System.out.println("  From Phone: " + phone);
        System.out.println("  Regarding Listing ID: " + accommodation.getId());
        System.out.println("  Lister User: " + (accommodation.getListedBy() != null ? accommodation.getListedBy().getUsername() : "N/A"));

        JOptionPane.showMessageDialog(mainPanel,
                "Message sending functionality is not yet implemented.\nDetails logged to console.",
                "Send Message (Placeholder)",
                JOptionPane.INFORMATION_MESSAGE);
    }


    // --- **REVISED Image Loading** ---
    // Pass the container panel to calculate available width
    private void loadImageAsync(JLabel imgLabel, JPanel imgContainer) {
        imgLabel.setIcon(null);
        imgLabel.setText("Loading image...");
        imgLabel.setPreferredSize(null); // Let layout manager decide initially

        List<String> imageUrls = accommodation.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty() &&
                imageUrls.get(0) != null && !imageUrls.get(0).trim().isEmpty()) {

            String imageUrlString = imageUrls.get(0).trim();

            SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    // Only load the original image here
                    try {
                        URL imageUrl = new URL(imageUrlString);
                        // Load into memory fully
                        BufferedImage loadedImage = javax.imageio.ImageIO.read(imageUrl);
                        if (loadedImage == null) {
                            System.err.println("Failed to decode image: " + imageUrlString);
                            return null;
                        }
                        return new ImageIcon(loadedImage); // Return icon with original image
                    } catch (MalformedURLException e) {
                        System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon originalIcon = get(); // Get the original icon (or null)
                        int availableWidth = 0;
                        if (originalIcon != null && originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {

                            // Get container width (subtract borders/insets if any)
                            availableWidth = imgContainer.getWidth();
                            if (availableWidth <= 0) { // If container not yet laid out, use a default guess
                                availableWidth = 600;
                            }
                            // Adjust for container's borders if they exist
                            Insets borders = imgContainer.getInsets();
                            availableWidth -= (borders.left + borders.right);


                            int originalWidth = originalIcon.getIconWidth();
                            int originalHeight = originalIcon.getIconHeight();

                            if (originalWidth <= 0 || availableWidth <= 0) {
                                throw new Exception("Invalid image or container dimensions");
                            }

                            // Calculate scaled height maintaining aspect ratio
                            int scaledHeight = (int) (((double) originalHeight / originalWidth) * availableWidth);

                            // Scale the image smoothly
                            Image scaledImage = originalIcon.getImage().getScaledInstance(availableWidth, scaledHeight, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(scaledImage);

                            imgLabel.setIcon(scaledIcon);
                            imgLabel.setText(null);
                            // Set preferred size of label to scaled size now
                            imgLabel.setPreferredSize(new Dimension(availableWidth, scaledHeight));

                        } else {
                            imgLabel.setText("No Image Available");
                            imgLabel.setPreferredSize(new Dimension(availableWidth > 0 ? availableWidth : 300, 200)); // Fallback size
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing/scaling image: " + e.getMessage());
                        // e.printStackTrace(); // Debugging
                        imgLabel.setText("Error Displaying Image");
                        imgLabel.setPreferredSize(new Dimension(300, 200)); // Fallback size
                        imgLabel.setIcon(null);
                    } finally {
                        // Ensure layout is updated after setting icon/text/size
                        imgContainer.revalidate();
                        imgContainer.repaint();
                        // Sometimes revalidating the parent scrollpane helps too
                        SwingUtilities.invokeLater(() -> {
                            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, mainPanel);
                            if (scrollPane != null) {
                                scrollPane.revalidate();
                                scrollPane.repaint();
                            } else {
                                mainPanel.revalidate();
                                mainPanel.repaint();
                            }
                        });
                    }
                }
            };
            imageLoader.execute();
        } else {
            imgLabel.setText("No Image Available");
            imgLabel.setPreferredSize(new Dimension(300, 200)); // Default size when no URL
            imgContainer.revalidate();
            imgContainer.repaint();
        }
    }


    // --- Getter for the main panel ---
    public JPanel getMainPanel() {
        return mainPanel;
    }
}