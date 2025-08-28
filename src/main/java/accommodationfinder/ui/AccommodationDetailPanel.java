package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.AccommodationService;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import org.jxmapviewer.painter.CompoundPainter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.geom.Point2D;
import java.awt.BasicStroke;

public class AccommodationDetailPanel extends JPanel {

    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;
    private final Long accommodationId;
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";
    private GeoPosition accommodationLocation = null; // Cache the geocoded location

    // UI Components
    private JLabel titleLabel;
    private JLabel imageLabel;
    private JTextArea detailsTextArea;
    private JTabbedPane imageContainerPanel;
    private JButton prevImageButton;
    private JButton nextImageButton;
    private JLabel imageCountLabel;

    // Contact Form Components
    private JTextField contactNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JButton sendMessageButton;
    private JLabel listerInfoLabel;

    // New Tab Components
    private JTabbedPane tabbedPane;
    private JPanel imagePanel;
    private JPanel locationPanel;
    private JLabel mapLabel;

    // State for Image Gallery
    private List<String> currentImageUrls;
    private int currentImageIndex = 0;

    // Current accommodation for map display
    private Accommodation currentAccommodation;

    // Formatters
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("en-ZA") );
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");

    // Constants
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color PLACEHOLDER_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BACKGROUND_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
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
        loadAccommodationDetails();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JButton backButton = new JButton("<- Back to Main View");
        styleButton(backButton, 14);
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());
        topPanel.add(backButton, BorderLayout.WEST);

        titleLabel = new JLabel("Loading...", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Center Section
        JPanel centerContentPanel = new JPanel(new BorderLayout(20, 10));
        centerContentPanel.setOpaque(false);

        // Left Section
        JPanel leftPanel = new JPanel(new BorderLayout(10, 15));
        leftPanel.setOpaque(false);

        // Create tabbed pane for Image/Location
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT + 40));
        tabbedPane.setBackground(BACKGROUND_COLOR);

        // Image Panel (original image gallery)
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(PLACEHOLDER_COLOR);

        imageLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(PLACEHOLDER_COLOR);
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Image Navigation Controls
        JPanel imageNavPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        imageNavPanel.setOpaque(false);
        prevImageButton = new JButton("< Prev");
        styleButton(prevImageButton, 13);
        nextImageButton = new JButton("Next >");
        styleButton(nextImageButton, 13);
        imageCountLabel = new JLabel("Image 0 of 0");
        imageCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        prevImageButton.addActionListener(e -> showImageAtIndex(currentImageIndex - 1));
        nextImageButton.addActionListener(e -> showImageAtIndex(currentImageIndex + 1));

        imageNavPanel.add(prevImageButton);
        imageNavPanel.add(imageCountLabel);
        imageNavPanel.add(nextImageButton);
        imagePanel.add(imageNavPanel, BorderLayout.SOUTH);

        // Location Panel (interactive map)
        locationPanel = new JPanel(new BorderLayout());
        locationPanel.setBackground(BACKGROUND_COLOR);

        mapLabel = new JLabel("Loading map...", SwingConstants.CENTER);
        mapLabel.setOpaque(true);
        mapLabel.setBackground(PLACEHOLDER_COLOR);
        mapLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        mapLabel.setVerticalAlignment(SwingConstants.CENTER);
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mapLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        locationPanel.add(mapLabel, BorderLayout.CENTER);

        // Add tabs
        tabbedPane.addTab("Images", imagePanel);
        tabbedPane.addTab("Location", locationPanel);

        // Set the original imageContainerPanel to be the tabbedPane
        imageContainerPanel = tabbedPane;

        leftPanel.add(imageContainerPanel, BorderLayout.NORTH);

        // Details Area
        detailsTextArea = new JTextArea("Loading details...");
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setWrapStyleWord(true);
        detailsTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

        // Contact Form
        JPanel contactPanel = createContactPanel();
        centerContentPanel.add(contactPanel, BorderLayout.EAST);

        add(centerContentPanel, BorderLayout.CENTER);

        // Add tab change listener to load map when Location tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Location tab
                loadInteractiveMap();
            }
        });
    }
    private void loadInteractiveMap() {
        if (currentAccommodation == null) {
            mapLabel.setText("Accommodation data not loaded");
            return;
        }

        SwingWorker<JXMapViewer, Void> mapLoader = new SwingWorker<>() {
            @Override
            protected JXMapViewer doInBackground() throws Exception {
                // Create map
                TileFactoryInfo info = new OSMTileFactoryInfo();
                DefaultTileFactory tileFactory = new DefaultTileFactory(info);
                JXMapViewer mapViewer = new JXMapViewer();
                mapViewer.setTileFactory(tileFactory);

                // Geocode the accommodation address if not already cached
                if (accommodationLocation == null) {
                    accommodationLocation = geocodeAddress(currentAccommodation);
                }

                // Use geocoded location or fallback to Cape Town
                GeoPosition location = accommodationLocation != null ?
                        accommodationLocation : new GeoPosition(-33.9249, 18.4241);

                mapViewer.setZoom(4); // Zoom in more to see the pin clearly
                mapViewer.setAddressLocation(location);

                // Create waypoint (pin) for the accommodation
                Waypoint waypoint = new DefaultWaypoint(location);
                Set<Waypoint> waypoints = new HashSet<>(Arrays.asList(waypoint));

                // Create waypoint painter (this draws the pin)
                RedWaypointPainter waypointPainter = new RedWaypointPainter();
                waypointPainter.setWaypoints(waypoints);

                // Set up painters
                CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(waypointPainter);
                mapViewer.setOverlayPainter(painter);

                // Add mouse listeners
                PanMouseInputListener mia = new PanMouseInputListener(mapViewer);
                mapViewer.addMouseListener(mia);
                mapViewer.addMouseMotionListener(mia);
                mapViewer.addMouseListener(new CenterMapListener(mapViewer));
                mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
                mapViewer.addKeyListener(new PanKeyListener(mapViewer));

                return mapViewer;
            }

            @Override
            protected void done() {
                try {
                    JXMapViewer mapViewer = get();

                    JPanel mapContainer = new JPanel(new BorderLayout());
                    mapContainer.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
                    mapContainer.add(mapViewer, BorderLayout.CENTER);

                    // Address info panel
                    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    infoPanel.setOpaque(false);

                    String address = currentAccommodation.getAddress() + ", " + currentAccommodation.getCity();
                    if (currentAccommodation.getPostalCode() != null) {
                        address += ", " + currentAccommodation.getPostalCode();
                    }

                    // Add geocoding status indicator
                    String locationStatus = accommodationLocation != null ? "📍 " : "📍 (approx.) ";
                    JLabel addressLabel = new JLabel(locationStatus + address);
                    addressLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
                    infoPanel.add(addressLabel);

                    mapContainer.add(infoPanel, BorderLayout.NORTH);

                    locationPanel.remove(mapLabel);
                    locationPanel.add(mapContainer, BorderLayout.CENTER);
                    locationPanel.revalidate();
                    locationPanel.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                    mapLabel.setText("Error loading map: " + e.getMessage());
                }
            }
        };

        mapLoader.execute();
    }

    private GeoPosition geocodeAddress(Accommodation accommodation) {
        try {
            // Build the address string
            StringBuilder addressBuilder = new StringBuilder();
            addressBuilder.append(accommodation.getAddress());
            addressBuilder.append(", ").append(accommodation.getCity());
            if (accommodation.getPostalCode() != null && !accommodation.getPostalCode().trim().isEmpty()) {
                addressBuilder.append(", ").append(accommodation.getPostalCode());
            }
            addressBuilder.append(", South Africa"); // Assuming all addresses are in South Africa

            String fullAddress = addressBuilder.toString();
            System.out.println("Geocoding address: " + fullAddress);

            // URL encode the address
            String encodedAddress = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);

            // Build the API request URL
            String requestUrl = NOMINATIM_API_URL + "?q=" + encodedAddress +
                    "&format=json&limit=1&countrycodes=za"; // Limit to South Africa

            // Make HTTP request
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "AccommodationFinder/1.0");
            connection.setConnectTimeout(5000); // 5 second timeout
            connection.setReadTimeout(10000); // 10 second timeout

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON response manually
                    String jsonResponse = response.toString();

                    // Simple regex-based JSON parsing for lat/lon
                    Pattern latPattern = Pattern.compile("\"lat\"\\s*:\\s*\"?([+-]?\\d*\\.?\\d+)\"?");
                    Pattern lonPattern = Pattern.compile("\"lon\"\\s*:\\s*\"?([+-]?\\d*\\.?\\d+)\"?");

                    Matcher latMatcher = latPattern.matcher(jsonResponse);
                    Matcher lonMatcher = lonPattern.matcher(jsonResponse);

                    if (latMatcher.find() && lonMatcher.find()) {
                        try {
                            double lat = Double.parseDouble(latMatcher.group(1));
                            double lon = Double.parseDouble(lonMatcher.group(1));

                            System.out.println("Geocoded to: " + lat + ", " + lon);
                            return new GeoPosition(lat, lon);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing coordinates: " + e.getMessage());
                        }
                    }
                }
            } else {
                System.err.println("Geocoding API returned status code: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Error geocoding address: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Return null if geocoding fails
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
        listerInfoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
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
        styleButton(sendMessageButton, 12);
        sendMessageButton.addActionListener(e -> handleSendMessage());
        contactPanel.add(sendMessageButton, gbc);

        // Vertical glue
        gbc.gridy++;
        gbc.weighty = 1.0;
        contactPanel.add(Box.createVerticalGlue(), gbc);

        return contactPanel;
    }

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

        // Re-check
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name and email.",

                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) { // Very basic validation
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listerInfoLabel.getText().startsWith("Listed by: ") && !listerInfoLabel.getText().endsWith("Unknown User")) {
        }

        System.out.println("--- Sending Message (Placeholder) ---");
        System.out.println("To Lister: "); // listerContactInfo
        System.out.println("From Name: " + name);
        System.out.println("From Email: " + email);
        System.out.println("From Phone: " + phone);
        System.out.println("Regarding Listing ID: " + accommodationId);
        System.out.println("-------------------------------------");

        JOptionPane.showMessageDialog(this, "Message sent to the lister" +
                "Message to Lister", "Message Sent", JOptionPane.INFORMATION_MESSAGE);

        contactNameField.setText("");
        contactEmailField.setText("");
        contactPhoneField.setText("");
        updateSendButtonState();
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
                        currentAccommodation = accommodation; // Store for map usage
                        populateUI(accommodation);
                        currentImageUrls = accommodation.getImageUrls(); // Store URLs
                        currentImageIndex = 0;
                        showImageAtIndex(currentImageIndex); // Load the first image
                    } else {
                        displayError("Accommodation Not Found",
                                "The requested accommodation listing could not be found.");
                        sendMessageButton.setEnabled(false);
                        prevImageButton.setEnabled(false);
                        nextImageButton.setEnabled(false);
                        imageCountLabel.setText("Image 0 of 0");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    e.printStackTrace();
                    displayError("Error Loading",
                            "An error occurred while loading accommodation details:\n" + cause.getMessage());
                    sendMessageButton.setEnabled(false);
                    prevImageButton.setEnabled(false);
                    nextImageButton.setEnabled(false);
                    imageCountLabel.setText("Image 0 of 0");
                } catch (Exception e) {
                    e.printStackTrace();
                    displayError("UI Update Error",
                            "An unexpected error occurred while displaying details:\n" + e.getMessage());
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

    // Optional: Method to handle multiple locations if you want to show multiple pins
    private Set<Waypoint> createWaypointsForMultipleLocations(List<Accommodation> accommodations) {
        Set<Waypoint> waypoints = new HashSet<>();

        for (Accommodation acc : accommodations) {
            GeoPosition location = geocodeAddress(acc);
            if (location != null) {
                waypoints.add(new DefaultWaypoint(location));
            }
        }

        return waypoints;
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
            return;
        }

        currentImageIndex = index;
        loadImageAsync(currentImageIndex, IMG_WIDTH, IMG_HEIGHT);

        // Update navigation state
        int totalImages = currentImageUrls.size();
        imageCountLabel.setText("Image " + (currentImageIndex + 1) + " of " + totalImages);
        prevImageButton.setEnabled(currentImageIndex > 0);
        nextImageButton.setEnabled(currentImageIndex < totalImages - 1);
    }


    // image loading with better scaling
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
                Image scaledImage = null;

                try {
                    URL imageUrl = new URL(imageUrlString.trim());
                    BufferedImage originalImage = null;
                    try (InputStream is = imageUrl.openStream()) {
                        originalImage = ImageIO.read(is);
                    } // Stream is automatically e

                    // Check if ImageIO successfully read the image including WebP via plugin
                    if (originalImage == null) {
                        System.err.println("Failed to load image using ImageIO (unsupported format or error): " +
                                imageUrlString);
                        return null;
                    }

                    // Proceed with scaling logic if image was loaded
                    int originalWidth = originalImage.getWidth(null);
                    int originalHeight = originalImage.getHeight(null);

                    if (originalWidth <= 0 || originalHeight <= 0) {
                        System.err.println("Invalid image dimensions for: " + imageUrlString);
                        return null;
                    }

                    // Calculate scaled dimensions maintaining aspect ratio
                    double scale = Math.min((double) targetWidth / originalWidth, (double) targetHeight / originalHeight);
                    int scaledWidth = (int) (originalWidth * scale);
                    int scaledHeight = (int) (originalHeight * scale);

                    scaledWidth = Math.max(1, scaledWidth);
                    scaledHeight = Math.max(1, scaledHeight);


                    // Create BufferedImage for higher quality scaling
                    BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
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
                } catch (IIOException e) { // Catch ImageIO errors
                    System.err.println("ImageIO error loading/reading image: " + imageUrlString + " - " + e.getMessage());
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    System.err.println("IO error loading image stream: " + imageUrlString + " - " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    System.err.println("General error loading/scaling image: " + imageUrlString + " - " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }


            @Override
            protected void done() {
                try {
                    ImageIcon scaledIcon = get();
                    if (scaledIcon != null) {
                        imageLabel.setIcon(scaledIcon);
                        imageLabel.setText(null);
                        imageLabel.setBackground(BACKGROUND_COLOR);
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
                } catch (Exception e) {
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


    // Helper for formatting price frequency
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

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }

    private static class RedWaypointPainter extends WaypointPainter<Waypoint> {

        public RedWaypointPainter() {
            setRenderer(new RedWaypointRenderer());
        }

        private static class RedWaypointRenderer implements WaypointRenderer<Waypoint> {
            @Override
            public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint) {
                Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());

                int x = (int) point.getX();
                int y = (int) point.getY();

                // Draw pin shape (teardrop)
                int pinWidth = 32;
                int pinHeight = 48;
                int pinX = x - pinWidth/2;
                int pinY = y - pinHeight;

                // Draw shadow
                g.setColor(new Color(0, 0, 0, 50));
                g.fillOval(pinX + 2, pinY + 2, pinWidth, pinWidth);
                int[] shadowXPoints = {x + 2, pinX + 2, pinX + pinWidth + 2};
                int[] shadowYPoints = {y + 2, pinY + pinWidth/2 + 2, pinY + pinWidth/2 + 2};
                g.fillPolygon(shadowXPoints, shadowYPoints, 3);

                // Draw red pin body (circle)
                g.setColor(Color.RED);
                g.fillOval(pinX, pinY, pinWidth, pinWidth);

                // Draw pin point (triangle)
                int[] xPoints = {x, pinX, pinX + pinWidth};
                int[] yPoints = {y, pinY + pinWidth/2, pinY + pinWidth/2};
                g.fillPolygon(xPoints, yPoints, 3);

                // Add white border to circle
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(3));
                g.drawOval(pinX, pinY, pinWidth, pinWidth);

                // Add center dot
                g.setColor(Color.WHITE);
                g.fillOval(x - 6, pinY + pinWidth/2 - 6, 12, 12);

            }
        }
    }

    // Method to return this panel for MainWindow
    public JPanel getDetailPanel() {
        return this;
    }
}