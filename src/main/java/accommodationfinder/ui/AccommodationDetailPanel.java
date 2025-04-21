package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AccommodationDetailPanel extends JPanel {

    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;
    private final Long accommodationId;

    // UI Components
    private JLabel titleLabel;
    private JLabel imageLabel;
    private JTextArea detailsTextArea;
    private JPanel imageContainerPanel;
    private JButton prevImageButton;
    private JButton nextImageButton;
    private JLabel imageCountLabel;

    // Contact Form Components
    private JTextField contactNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JButton sendMessageButton;
    private JLabel listerInfoLabel;

    // State for Image Gallery
    private List<String> currentImageUrls;
    private int currentImageIndex = 0;

    // Formatters
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");

    // Constants
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color PLACEHOLDER_COLOR = new Color(230, 230, 230);
    private static final int IMG_WIDTH = 550;
    private static final int IMG_HEIGHT = 400;

    public AccommodationDetailPanel(AccommodationService accommodationService, MainWindow mainWindow, Long accommodationId) {
        this.accommodationService = accommodationService;
        this.mainWindow = mainWindow;
        this.accommodationId = accommodationId;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 25, 15, 25));
        setBackground(BACKGROUND_COLOR);

        initComponents();
        setupContactFormListeners();
        //prefillContactForm();      // Prefill form if user is logged in
        loadAccommodationDetails();
    }

    private void initComponents() {
        // --- Top Section (Back Button + Title) ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JButton backButton = new JButton("<- Back to Main View");
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());
        topPanel.add(backButton, BorderLayout.WEST);

        titleLabel = new JLabel("Loading...", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Center Section (Image Gallery + Details + Contact)
        JPanel centerContentPanel = new JPanel(new BorderLayout(20, 10));
        centerContentPanel.setOpaque(false);

        // Left Side (Image Gallery + Details below)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 15));
        leftPanel.setOpaque(false);

        // Image Gallery Panel
        imageContainerPanel = new JPanel(new BorderLayout());
        imageContainerPanel.setBackground(PLACEHOLDER_COLOR);
        imageContainerPanel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT + 40));

        imageLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(PLACEHOLDER_COLOR);
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageContainerPanel.add(imageLabel, BorderLayout.CENTER);

        // Image Navigation Controls
        JPanel imageNavPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        imageNavPanel.setOpaque(false);
        prevImageButton = new JButton("< Prev");
        nextImageButton = new JButton("Next >");
        imageCountLabel = new JLabel("Image 0 of 0");
        imageCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));


        prevImageButton.addActionListener(e -> showImageAtIndex(currentImageIndex - 1));
        nextImageButton.addActionListener(e -> showImageAtIndex(currentImageIndex + 1));

        imageNavPanel.add(prevImageButton);
        imageNavPanel.add(imageCountLabel);
        imageNavPanel.add(nextImageButton);
        imageContainerPanel.add(imageNavPanel, BorderLayout.SOUTH);

        leftPanel.add(imageContainerPanel, BorderLayout.NORTH);

        // Details Area
        detailsTextArea = new JTextArea("Loading details...");
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setWrapStyleWord(true);
        detailsTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Details"),
                new EmptyBorder(5, 5, 5, 5)
        ));
        detailsTextArea.setBackground(BACKGROUND_COLOR);

        JScrollPane detailsScrollPane = new JScrollPane(detailsTextArea);

        detailsScrollPane.setBorder(null);
        detailsScrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        leftPanel.add(detailsScrollPane, BorderLayout.CENTER);

        centerContentPanel.add(leftPanel, BorderLayout.CENTER);

        // --- Right Side (Contact Form) ---
        JPanel contactPanel = createContactPanel();
        centerContentPanel.add(contactPanel, BorderLayout.EAST);

        add(centerContentPanel, BorderLayout.CENTER);
    }

    private JPanel createContactPanel() {
        JPanel contactPanel = new JPanel();
        contactPanel.setLayout(new GridBagLayout());
        contactPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Contact Lister"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        contactPanel.setOpaque(false);
        contactPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Lister Info
        listerInfoLabel = new JLabel("Listed by: Loading...");
        listerInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridwidth = 3;
        contactPanel.add(listerInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        contactPanel.add(new JLabel("Your Name: *"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contactNameField = new JTextField(15);
        contactPanel.add(contactNameField, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;

        gbc.gridy++;
        gbc.gridx = 0;
        contactPanel.add(new JLabel("Your Email: *"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contactEmailField = new JTextField(15);
        contactPanel.add(contactEmailField, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;

        gbc.gridy++;
        gbc.gridx = 0;
        contactPanel.add(new JLabel("Your Phone: "), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contactPhoneField = new JTextField(15);
        contactPanel.add(contactPhoneField, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(e -> handleSendMessage());
        contactPanel.add(sendMessageButton, gbc);

        // Vertical glue
        gbc.gridy++;
        gbc.weighty = 1.0;
        contactPanel.add(Box.createVerticalGlue(), gbc);

        return contactPanel;
    }

    // Prefill contact form if user is logged in
//    private void prefillContactForm() {
//        if (loggedInUser != null) {
//            contactNameField.setText(loggedInUser.getUsername()); // Or a dedicated name field if User has one
//            contactEmailField.setText(loggedInUser.getEmail());
//            // Optionally prefill phone if available
//            // contactPhoneField.setText(loggedInUser.getPhoneNumber());
//            updateSendButtonState(); // Check if prefilled fields are valid
//        }
//    }

    // Setup listeners to enable/disable send button
    private void setupContactFormListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSendButtonState();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSendButtonState();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        contactNameField.getDocument().addDocumentListener(listener);
        contactEmailField.getDocument().addDocumentListener(listener);
    }

    // Enable send button only if Name and Email are filled
    private void updateSendButtonState() {
        boolean enabled = !contactNameField.getText().trim().isEmpty()
                && !contactEmailField.getText().trim().isEmpty();
        // Basic email format check
        enabled = enabled && contactEmailField.getText().trim().contains("@");
        sendMessageButton.setEnabled(enabled);
    }


    private void handleSendMessage() {
        String name = contactNameField.getText().trim();
        String email = contactEmailField.getText().trim();
        String phone = contactPhoneField.getText().trim();

        // Re-check just in case
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name and email.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) { // Very basic validation
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: Retrieve the lister's actual email address or contact mechanism
        //       from the 'accommodation' object (fetched in loadAccommodationDetails)
        //       or via another service call if needed.
        String listerContactInfo = "Lister's Email/ID (Not Implemented Yet)";
        if (listerInfoLabel.getText().startsWith("Listed by: ") && !listerInfoLabel.getText().endsWith("Unknown User")) {
            // Ideally fetch email associated with the username
            // listerContactInfo = fetchListerEmail(listerInfoLabel.getText().substring("Listed by: ".length()));
        }

        System.out.println("--- Sending Message (Placeholder) ---");
        System.out.println("To Lister: " + listerContactInfo);
        System.out.println("From Name: " + name);
        System.out.println("From Email: " + email);
        System.out.println("From Phone: " + phone);
        System.out.println("Regarding Listing ID: " + accommodationId);
        System.out.println("-------------------------------------");

        JOptionPane.showMessageDialog(this, "Message sent (placeholder).\nActual implementation needed.", "Message Sent", JOptionPane.INFORMATION_MESSAGE);

        // contactNameField.setText("");
        // contactEmailField.setText("");
        // contactPhoneField.setText("");
        // updateSendButtonState(); // Disable button again if fields are cleared
    }

    private void loadAccommodationDetails() {
        SwingWorker<Accommodation, Void> worker = new SwingWorker<>() {
            @Override
            protected Accommodation doInBackground() throws Exception {
                return accommodationService.getListingById(accommodationId);
            }

            @Override
            protected void done() {
                try {
                    Accommodation accommodation = get();
                    if (accommodation != null) {
                        populateUI(accommodation);
                        currentImageUrls = accommodation.getImageUrls(); // Store URLs
                        currentImageIndex = 0;
                        showImageAtIndex(currentImageIndex); // Load the first image
                    } else {
                        displayError("Accommodation Not Found", "The requested accommodation listing could not be found.");
                        sendMessageButton.setEnabled(false);
                        prevImageButton.setEnabled(false);
                        nextImageButton.setEnabled(false);
                        imageCountLabel.setText("Image 0 of 0");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    e.printStackTrace();
                    displayError("Error Loading", "An error occurred while loading accommodation details:\n" + cause.getMessage());
                    sendMessageButton.setEnabled(false);
                    prevImageButton.setEnabled(false);
                    nextImageButton.setEnabled(false);
                    imageCountLabel.setText("Image 0 of 0");
                } catch (Exception e) {
                    e.printStackTrace();
                    displayError("UI Update Error", "An unexpected error occurred while displaying details:\n" + e.getMessage());
                    sendMessageButton.setEnabled(false);
                    prevImageButton.setEnabled(false);
                    nextImageButton.setEnabled(false);
                    imageCountLabel.setText("Image 0 of 0");
                }
            }
        };
        worker.execute();
    }

    private void displayError(String title, String message) {
        titleLabel.setText(title);
        detailsTextArea.setText(message);
        imageLabel.setText("Error");
        imageLabel.setIcon(null);
        JOptionPane.showMessageDialog(AccommodationDetailPanel.this,
                message, title, JOptionPane.ERROR_MESSAGE);
    }


    private void populateUI(Accommodation acc) {
        titleLabel.setText(acc.getTitle());

        StringBuilder details = new StringBuilder();
        details.append("Address:\n").append(acc.getAddress()).append(", ").append(acc.getCity())
                .append(acc.getPostalCode() != null ? ", " + acc.getPostalCode() : "").append("\n\n");

        details.append("Price: ").append(currencyFormatter.format(acc.getPrice()))
                .append(" ").append(formatPriceFrequency(acc.getPriceFrequency())).append("\n");
        details.append("Type: ").append(acc.getType()).append("\n");
        details.append("Beds: ").append(acc.getBedrooms()).append(" | Baths: ").append(acc.getBathrooms())
                .append(" | Max Occupancy: ").append(acc.getMaxOccupancy()).append("\n\n");

        details.append("Description:\n").append(acc.getDescription()).append("\n\n");

        details.append("Features:\n");
        details.append("- Internet Included: ").append(acc.isInternetIncluded() ? "Yes" : "No").append("\n");
        details.append("- Utilities Included: ").append(acc.isUtilitiesIncluded() ? "Yes" : "No").append("\n");
        details.append("- Parking Available: ").append(acc.isParkingAvailable() ? "Yes" : "No").append("\n");
        details.append("- NSFAS Accredited: ").append(acc.isNsfasAccredited() ? "Yes" : "No").append("\n\n");

        if (acc.getLeaseTerm() != null && !acc.getLeaseTerm().isEmpty()){
            details.append("Lease Term: ").append(acc.getLeaseTerm()).append("\n");
        }

        details.append("Available From: ")
                .append(acc.getAvailableFrom() != null ? acc.getAvailableFrom().format(dateFormatter) : "N/A").append("\n");
        if (acc.getAvailableUntil() != null) {
            details.append("Available Until: ")
                    .append(acc.getAvailableUntil().format(dateFormatter)).append("\n");
        }

        detailsTextArea.setText(details.toString());
        detailsTextArea.setCaretPosition(0);

        // Populate Lister Info
        if (acc.getListedBy() != null) {
            listerInfoLabel.setText("Listed by: " + acc.getListedBy().getFullName());
        } else {
            listerInfoLabel.setText("Listed by: Unknown User");
        }
    }

    // Method to show image at a specific index
    private void showImageAtIndex(int index) {
        if (currentImageUrls == null || currentImageUrls.isEmpty()) {
            imageLabel.setText("No Images Available");
            imageLabel.setIcon(null);
            imageCountLabel.setText("Image 0 of 0");
            prevImageButton.setEnabled(false);
            nextImageButton.setEnabled(false);
            return;
        }

        // Validate index
        if (index < 0 || index >= currentImageUrls.size()) {
            System.err.println("showImageAtIndex: Invalid index " + index);
            return; // Index out of bounds
        }

        currentImageIndex = index;
        loadImageAsync(currentImageIndex, IMG_WIDTH, IMG_HEIGHT);

        // Update navigation state
        int totalImages = currentImageUrls.size();
        imageCountLabel.setText("Image " + (currentImageIndex + 1) + " of " + totalImages);
        prevImageButton.setEnabled(currentImageIndex > 0);
        nextImageButton.setEnabled(currentImageIndex < totalImages - 1);
    }


    // Enhanced image loading with better scaling
    private void loadImageAsync(int imageIndex, int targetWidth, int targetHeight) {
        imageLabel.setIcon(null);
        imageLabel.setText("Loading image...");

        if (currentImageUrls == null || imageIndex < 0 || imageIndex >= currentImageUrls.size()) {
            imageLabel.setText("No Image Available");
            return;
        }

        String imageUrlString = currentImageUrls.get(imageIndex);
        if (imageUrlString == null || imageUrlString.trim().isEmpty()) {
            imageLabel.setText("No Image Available");
            return;
        }

        SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    URL imageUrl = new URL(imageUrlString.trim());
                    ImageIcon originalIcon = new ImageIcon(imageUrl);

                    if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                        System.err.println("Failed to load image: " + imageUrlString + " (Status: " +
                                originalIcon.getImageLoadStatus() + ")");
                        return null;
                    }

                    Image originalImage = originalIcon.getImage();
                    int originalWidth = originalImage.getWidth(null);
                    int originalHeight = originalImage.getHeight(null);

                    if (originalWidth <= 0 || originalHeight <= 0) {
                        System.err.println("Invalid image dimensions for: " + imageUrlString);
                        return null; // Invalid image dimensions
                    }

                    // Calculate scaled dimensions maintaining aspect ratio
                    double scale = Math.min((double) targetWidth / originalWidth, (double) targetHeight / originalHeight);
                    int scaledWidth = (int) (originalWidth * scale);
                    int scaledHeight = (int) (originalHeight * scale);

                    // Create a BufferedImage for higher quality scaling
                    BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB); // Use ARGB for transparency
                    Graphics2D g2d = scaledBI.createGraphics();

                    // Apply rendering hints for better quality
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
                    g2d.dispose();

                    return new ImageIcon(scaledBI);

                } catch (MalformedURLException e) {
                    System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    // Catch broader exceptions during image processing
                    System.err.println("Error loading/scaling image: " + imageUrlString + " - " + e.getMessage());
                    e.printStackTrace(); // Print stack trace for debugging
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon scaledIcon = get();
                    if (scaledIcon != null) {
                        imageLabel.setIcon(scaledIcon);
                        imageLabel.setText(null); // Remove text
                        imageLabel.setBackground(BACKGROUND_COLOR); // Match background if image smaller than label
                    } else {
                        imageLabel.setText("Image Unavailable");
                        imageLabel.setIcon(null);
                        imageLabel.setBackground(PLACEHOLDER_COLOR);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error updating detail image label: " + e.getMessage());
                    e.printStackTrace();
                    imageLabel.setText("Error Loading Image");
                    imageLabel.setIcon(null);
                    imageLabel.setBackground(PLACEHOLDER_COLOR);
                } catch (Exception e) { // Catch other potential runtime exceptions
                    System.err.println("Unexpected error updating image label: " + e.getMessage());
                    e.printStackTrace();
                    imageLabel.setText("Error");
                    imageLabel.setIcon(null);
                    imageLabel.setBackground(PLACEHOLDER_COLOR);
                }
            }
        };
        imageLoader.execute();
    }


    // Helper for formatting price frequency (same as before)
    private String formatPriceFrequency(Accommodation.PriceFrequency frequency) {
        if (frequency == null) return "";
        return switch (frequency) {
            case PER_MONTH -> "/ Month";
            case PER_WEEK -> "/ week";
            case PER_SEMESTER -> "/ semester";
            case PER_NIGHT -> "/ night";
            case OTHER -> "";
        };
    }

    // Method to return this panel for MainWindow (same as before)
    public JPanel getDetailPanel() {
        return this;
    }
}