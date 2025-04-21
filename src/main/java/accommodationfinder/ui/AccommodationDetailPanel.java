package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Displays the detailed view of a single accommodation listing.
 */
public class AccommodationDetailPanel {

    private JPanel mainPanel;
    private final Accommodation accommodation;
    private final MainWindow mainWindow;
    private final AccommodationService accommodationService;

    // Components for the UI
    private JLabel titleLabel;
    private JLabel imageLabel; // For displaying the image
    private JTextArea detailsTextArea;
    private JLabel addressLabel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton sendMessageButton;
    private JButton backButton;

    // Define placeholder color
    private static final Color PLACEHOLDER_COLOR = new Color(220, 220, 220);
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);


    public AccommodationDetailPanel(Accommodation accommodation, AccommodationService accommodationService, MainWindow mainWindow) {
        this.accommodation = accommodation;
        this.mainWindow = mainWindow;
        this.accommodationService = accommodationService;
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10)); // Main layout
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15)); // Padding around the whole panel

        // --- Top Bar (Placeholder) ---
        mainPanel.add(createTopBarPlaceholder(), BorderLayout.NORTH);

        // --- Center Content Area---
        JPanel centerContentPanel = new JPanel(new GridBagLayout());
        centerContentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align components top-left

        // --- Search Bar Placeholder ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0); // Bottom margin
        centerContentPanel.add(createSearchPlaceholder(), gbc);
        gbc.insets = new Insets(5, 5, 5, 5); // Reset insets

        // --- Left Column (Title, Image, Details/Address) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6; // Left column takes ~60% width
        gbc.weighty = 1.0; // Allow vertical expansion
        gbc.fill = GridBagConstraints.BOTH; // Fill available space
        gbc.anchor = GridBagConstraints.NORTHWEST;
        centerContentPanel.add(createLeftPanel(), gbc);

        // --- Right Column (Contact Form) ---
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4; // Right column takes ~40% width
        gbc.weighty = 0; // Don't expand vertically beyond preferred size
        gbc.fill = GridBagConstraints.HORIZONTAL; // Only fill horizontally
        gbc.anchor = GridBagConstraints.NORTHEAST; // Align top-right
        centerContentPanel.add(createRightPanel(), gbc);


        mainPanel.add(centerContentPanel, BorderLayout.CENTER);

        // Populate fields with data
        populateData();
    }

    private JPanel createTopBarPlaceholder() {
        JPanel topBar = new JPanel(new BorderLayout(10,0));
        topBar.setBackground(BACKGROUND_COLOR);
        topBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel appTitleLabel = new JLabel("ResFinder");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        // Add a Back Button
        backButton = new JButton("<- Back to Listings");
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);
        leftPanel.add(appTitleLabel); // Add title next to back button


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setEnabled(false); // Placeholder appearance
        JButton loginButton = new JButton("Login");
        loginButton.setEnabled(false); // Placeholder appearance
        // Simple circle placeholder (could be improved with custom painting)
        JPanel profilePlaceholder = new JPanel();
        profilePlaceholder.setPreferredSize(new Dimension(30, 30));
        profilePlaceholder.setBackground(PLACEHOLDER_COLOR);
        profilePlaceholder.setBorder(new LineBorder(Color.GRAY));

        rightPanel.add(signUpButton);
        rightPanel.add(loginButton);
        rightPanel.add(profilePlaceholder);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createSearchPlaceholder() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(PLACEHOLDER_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY),
                new EmptyBorder(8, 10, 8, 10) // Internal padding
        ));
        JLabel searchLabel = new JLabel("Search listings");
        searchLabel.setForeground(Color.DARK_GRAY);
        searchPanel.add(searchLabel, BorderLayout.WEST);
        return searchPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setBorder(new EmptyBorder(10, 0, 10, 20)); // Right padding

        // Title Label
        titleLabel = new JLabel("Accommodation Listing Title..."); // Placeholder text
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Bottom margin

        // Image Panel Placeholder
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(PLACEHOLDER_COLOR);
        imagePanel.setPreferredSize(new Dimension(500, 350)); // Adjust size as needed
        imagePanel.setMinimumSize(new Dimension(300, 200));
        imagePanel.setBorder(new LineBorder(Color.GRAY));
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        imageLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(500, 350)); // Match panel size
        imageLabel.setOpaque(true);
        imageLabel.setBackground(PLACEHOLDER_COLOR);
        imagePanel.add(imageLabel, BorderLayout.CENTER);


        // Details/Address Area Placeholder
        JPanel detailsAddressPanel = new JPanel();
        detailsAddressPanel.setLayout(new BoxLayout(detailsAddressPanel, BoxLayout.Y_AXIS));
        detailsAddressPanel.setBackground(PLACEHOLDER_COLOR);
        detailsAddressPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY),
                new EmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        detailsAddressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsAddressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Limit height


        addressLabel = new JLabel("Address: ....................................");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detailsTitleLabel = new JLabel("Details:");
        detailsTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsTitleLabel.setBorder(new EmptyBorder(10, 0, 5, 0)); // Space above

        detailsTextArea = new JTextArea("....................................\n....................................\n....................................");
        detailsTextArea.setFont(new Font("Arial", Font.PLAIN, 13));
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setWrapStyleWord(true);
        detailsTextArea.setOpaque(false); // Make transparent to show panel background
        detailsTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane descriptionScrollPane = new JScrollPane(detailsTextArea);
        descriptionScrollPane.setBorder(null);
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.getViewport().setOpaque(false);
        descriptionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);


        detailsAddressPanel.add(addressLabel);
        detailsAddressPanel.add(detailsTitleLabel);
        detailsAddressPanel.add(descriptionScrollPane); // Add scrollpane


        // Add components to left panel
        leftPanel.add(titleLabel);
        leftPanel.add(imagePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space below image
        leftPanel.add(detailsAddressPanel);
        leftPanel.add(Box.createVerticalGlue()); // Push content up if space allows


        return leftPanel;
    }


    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 0)); // Left padding

        JLabel contactTitle = new JLabel("Contact:");
        contactTitle.setFont(new Font("Arial", Font.BOLD, 18));
        contactTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactTitle.setBorder(new EmptyBorder(0, 0, 15, 0)); // Bottom margin

        nameField = createFormField("Name:");
        emailField = createFormField("Email:");
        phoneField = createFormField("Phone number:");

        sendMessageButton = new JButton("Send Message");
        sendMessageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Set preferred and maximum size to prevent stretching
        Dimension buttonSize = new Dimension(150, 35);
        sendMessageButton.setPreferredSize(buttonSize);
        sendMessageButton.setMaximumSize(buttonSize);
        sendMessageButton.setMinimumSize(buttonSize);

        sendMessageButton.addActionListener(e -> {
            // Placeholder action
            JOptionPane.showMessageDialog(mainPanel, "Send Message clicked (Not implemented)", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        rightPanel.add(contactTitle);
        rightPanel.add(createFieldPanel(new JLabel("Name:"), nameField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(createFieldPanel(new JLabel("Email:"), emailField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(createFieldPanel(new JLabel("Phone number:"), phoneField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space above button
        rightPanel.add(sendMessageButton);
        rightPanel.add(Box.createVerticalGlue()); // Pushes content up

        // Set maximum width for the right panel
        rightPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));

        return rightPanel;
    }

    // Helper to create a text field with placeholder look
    private JTextField createFormField(String labelText) {
        JTextField field = new JTextField(20); // Approx width
        field.setText("...................................."); // Placeholder dots
        field.setForeground(Color.GRAY);
        field.setBackground(PLACEHOLDER_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.DARK_GRAY),
                new EmptyBorder(5, 5, 5, 5) // Internal padding
        ));
        // Add focus listener to clear placeholder if needed (optional)
        return field;
    }

    // Helper to create a panel for label + field
    private JPanel createFieldPanel(JLabel label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5,0)); // Use BorderLayout for better alignment
        panel.setBackground(BACKGROUND_COLOR);
        label.setPreferredSize(new Dimension(100, 25)); // Give label fixed width
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Limit the height of the field panel
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 5));
        return panel;
    }


    private void populateData() {
        if (accommodation == null) {
            // Handle case where accommodation data is missing
            titleLabel.setText("Listing Not Found");
            // Disable contact form etc.
            return;
        }

        titleLabel.setText(accommodation.getTitle());
        addressLabel.setText("Address: " + (accommodation.getAddress() != null ? accommodation.getAddress() : "N/A"));
        detailsTextArea.setText(accommodation.getDescription() != null ? accommodation.getDescription() : "No details provided.");
        detailsTextArea.setCaretPosition(0); // Scroll to top

        // TODO: Populate contact fields if needed (maybe landlord info?)
        // For now, leave the placeholders
        nameField.setText(""); // Clear placeholder
        emailField.setText("");
        phoneField.setText("");
        nameField.setBackground(Color.WHITE); // Change background when real
        emailField.setBackground(Color.WHITE);
        phoneField.setBackground(Color.WHITE);
        nameField.setForeground(Color.BLACK);
        emailField.setForeground(Color.BLACK);
        phoneField.setForeground(Color.BLACK);


        // Load the first image
        loadImageAsync(imageLabel, 500, 350); // Use the size defined for the image panel
    }

    // Reuse image loading logic (can be moved to a utility class later)
    private void loadImageAsync(JLabel imgLabel, int targetWidth, int targetHeight) {
        imgLabel.setIcon(null);
        imgLabel.setText("Loading...");

        List<String> imageUrls = accommodation.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty() &&
                imageUrls.get(0) != null && !imageUrls.get(0).trim().isEmpty()) {

            String imageUrlString = imageUrls.get(0).trim(); // Get the first image URL

            SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    try {
                        URL imageUrl = new URL(imageUrlString);
                        ImageIcon originalIcon = new ImageIcon(imageUrl);

                        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                            System.err.println("Failed to load image: " + imageUrlString);
                            return null;
                        }

                        Image image = originalIcon.getImage();
                        // Scale to fit while maintaining aspect ratio (more complex) OR just scale to fit
                        Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);

                    } catch (MalformedURLException e) {
                        System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    } catch (Exception e) {
                        System.err.println("Error loading/scaling image: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon scaledIcon = get();
                        if (scaledIcon != null) {
                            imgLabel.setIcon(scaledIcon);
                            imgLabel.setText(null); // Remove "Loading..." text
                        } else {
                            imgLabel.setText("No Image Available");
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating image label: " + e.getMessage());
                        e.printStackTrace();
                        imgLabel.setText("Error Loading Image");
                        imgLabel.setIcon(null);
                    }
                }
            };
            imageLoader.execute();
        } else {
            imgLabel.setText("No Image Available");
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
}