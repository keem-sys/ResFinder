package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import accommodationfinder.map.MapManager;
import accommodationfinder.service.AccommodationService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class AccommodationDetailPanel extends JPanel {

    private final AccommodationService accommodationService;
    private final MainWindow mainWindow;
    private final MapManager mapManager;
    private Accommodation currentAccommodation;
    private Long currentAccommodationId;


    // UI Components
    private JLabel titleLabel;
    private JTextArea detailsTextArea;
    private JLabel mapLabel;
    private JPanel locationPanel;
    private JTabbedPane tabbedPane;


    // Sub-Panels
    private ImageGalleryPanel imageGalleryPanel;
    private ContactListerPanel contactListerPanel;


    // Formatters & Constants
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("en-ZA") );
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final int IMG_WIDTH = 550;
    private static final int IMG_HEIGHT = 400;


    public AccommodationDetailPanel(AccommodationService accommodationService, MainWindow mainWindow) {
        this.accommodationService = accommodationService;
        this.mainWindow = mainWindow;
        this.mapManager = new MapManager();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 25, 15, 25));
        setBackground(BACKGROUND_COLOR);

        initComponents();
    }

    private void initComponents() {
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        JButton backButton = new JButton("<- Back to Main View");
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());
        topPanel.add(backButton, BorderLayout.WEST);
        titleLabel = new JLabel("Loading...", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerContentPanel = new JPanel(new BorderLayout(20, 10));
        centerContentPanel.setOpaque(false);

        // Left Panel (Image Gallery and Details)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 15));
        leftPanel.setOpaque(false);

        imageGalleryPanel = new ImageGalleryPanel(IMG_WIDTH, IMG_HEIGHT);
        contactListerPanel = new ContactListerPanel();

        locationPanel = new JPanel(new BorderLayout());
        locationPanel.setBackground(BACKGROUND_COLOR);
        mapLabel = new JLabel("Select this tab to load the map", SwingConstants.CENTER);
        mapLabel.setOpaque(true);
        mapLabel.setBackground(new Color(230, 230, 230));
        locationPanel.add(mapLabel, BorderLayout.CENTER);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Images", imageGalleryPanel);
        tabbedPane.addTab("Location", locationPanel);
        leftPanel.add(tabbedPane, BorderLayout.NORTH);

        detailsTextArea = new JTextArea("Loading details...");
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setWrapStyleWord(true);
        detailsTextArea.setBackground(BACKGROUND_COLOR);
        detailsTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Details"), new EmptyBorder(5, 5, 5, 5)));
        JScrollPane detailsScrollPane = new JScrollPane(detailsTextArea);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        leftPanel.add(detailsScrollPane, BorderLayout.CENTER);

        centerContentPanel.add(leftPanel, BorderLayout.CENTER);
        centerContentPanel.add(contactListerPanel, BorderLayout.EAST);

        add(centerContentPanel, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                if (locationPanel.getComponent(0) == mapLabel) {
                    loadInteractiveMap();
                }
            }
        });
    }

    public void loadAccommodationDetails(Long accommodationId) {
        this.currentAccommodationId = accommodationId;
        resetUIForLoading();

        SwingWorker<Accommodation, Void> worker = new SwingWorker<>() {
            @Override
            protected Accommodation doInBackground() throws Exception {
                return accommodationService.getListingById(currentAccommodationId);
            }

            @Override
            protected void done() {
                try {
                    Accommodation accommodation = get();
                    if (accommodation != null) {
                        currentAccommodation = accommodation;
                        populateUI(accommodation);
                        imageGalleryPanel.setImageUrls(accommodation.getImageUrls());
                        contactListerPanel.updatePanelInfo(accommodation.getId(), accommodation.getListedBy());
                    } else {
                        displayError("Accommodation Not Found", "The requested listing could not be found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    displayError("Error Loading", "An error occurred while loading details: " +
                            e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void resetUIForLoading() {
        titleLabel.setText("Loading...");
        detailsTextArea.setText("Loading details...");
        detailsTextArea.setCaretPosition(0);
        imageGalleryPanel.setImageUrls(null);
        contactListerPanel.reset();

        locationPanel.removeAll();
        mapLabel = new JLabel("Select this tab to load the map", SwingConstants.CENTER);
        mapLabel.setOpaque(true);
        mapLabel.setBackground(new Color(230, 230, 230));
        locationPanel.add(mapLabel, BorderLayout.CENTER);
        tabbedPane.setSelectedIndex(0);

        revalidate();
        repaint();
    }

    private void loadInteractiveMap() {
        if (currentAccommodation == null) {
            mapLabel.setText("Accommodation data not loaded");
            return;
        }

        mapLabel.setText("Loading map...");

        SwingWorker<JPanel, Void> mapLoader = new SwingWorker<>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                Component mapViewer = mapManager.createMapViewer(currentAccommodation);

                JPanel mapContainer = new JPanel(new BorderLayout());
                mapContainer.add(mapViewer, BorderLayout.CENTER);

                // Address info panel
                JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                infoPanel.setOpaque(false);
                String address = currentAccommodation.getAddress() + ", " + currentAccommodation.getCity();
                JLabel addressLabel = new JLabel("📍 " + address);
                addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                infoPanel.add(addressLabel);
                mapContainer.add(infoPanel, BorderLayout.NORTH);

                return mapContainer;
            }

            @Override
            protected void done() {
                try {
                    JPanel mapContainer = get();
                    locationPanel.remove(mapLabel);
                    locationPanel.add(mapContainer, BorderLayout.CENTER);
                    locationPanel.revalidate();
                    locationPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    locationPanel.remove(mapLabel); // Remove loading label
                    JLabel errorLabel = new JLabel("Error loading map: " + e.getMessage());
                    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    locationPanel.add(errorLabel);
                    locationPanel.revalidate();
                    locationPanel.repaint();
                }
            }
        };

        mapLoader.execute();
    }


    private void populateUI(Accommodation acc) {
        titleLabel.setText(acc.getTitle());

        StringBuilder details = new StringBuilder();
        details.append("Address:\n").append(acc.getAddress()).append(", ").append(acc.getCity())
                .append(acc.getPostalCode() != null ? ", " + acc.getPostalCode() : "").append("\n\n");

        details.append("Price: ").append(currencyFormatter.format(acc.getPrice()))
                .append(" ").append(formatPriceFrequency(acc.getPriceFrequency())).append("\n"); // You need to re-add this helper method
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
    }

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

    private void displayError(String title, String message) {
        titleLabel.setText(title);
        detailsTextArea.setText(message);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getDetailPanel() {
        return this;
    }
}