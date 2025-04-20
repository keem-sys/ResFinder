package accommodationfinder.ui;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import accommodationfinder.listing.Accommodation; // Import your Accommodation class

    public class DetailedAccommodationViewPanel extends JPanel {

        private final MainWindow mainWindow;

        private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
        private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM<ctrl98> HH:mm");

        private JLabel titleLabel;
        private JLabel priceLabel;
        private JTextArea descriptionTextArea;
        private JLabel addressLabel;
        private JLabel bedroomsLabel;
        private JLabel bathroomsLabel;
        private JLabel availabilityLabel;
        // Add more labels/components as needed for other details

        public DetailedAccommodationViewPanel(MainWindow mainWindow) {
            this.mainWindow = mainWindow;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            initComponents();
        }

        private void initComponents() {
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

            titleLabel = new JLabel();
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            detailsPanel.add(titleLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            priceLabel = new JLabel();
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            detailsPanel.add(priceLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            addressLabel = new JLabel();
            addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(addressLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bedroomsLabel = new JLabel();
            infoPanel.add(bedroomsLabel);
            infoPanel.add(new JLabel(" | "));
            bathroomsLabel = new JLabel();
            infoPanel.add(bathroomsLabel);
            detailsPanel.add(infoPanel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            availabilityLabel = new JLabel();
            availabilityLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            detailsPanel.add(availabilityLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            descriptionTextArea = new JTextArea();
            descriptionTextArea.setLineWrap(true);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setEditable(false);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
            descriptionScrollPane.setPreferredSize(new Dimension(400, 150));
            detailsPanel.add(descriptionScrollPane);

            add(detailsPanel, BorderLayout.CENTER);

            // Back Button
            JButton backButton = new JButton("Back to Listings");
            backButton.addActionListener(e -> mainWindow.showMainApplicationView()); // Assuming this method exists
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.add(backButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        public void displayAccommodation(Accommodation accommodation) {
            titleLabel.setText(accommodation.getTitle());
            priceLabel.setText(currencyFormatter.format(accommodation.getPrice()) + " " + formatPriceFrequency(accommodation.getPriceFrequency()));
            addressLabel.setText(accommodation.getAddress() + ", " + accommodation.getCity());
            bedroomsLabel.setText(accommodation.getBedrooms() + " Bed");
            bathroomsLabel.setText(accommodation.getBathrooms() + " Bath");

            if (accommodation.getAvailableFrom() != null) {
                availabilityLabel.setText("Available from: " + accommodation.getAvailableFrom().format(dateFormatter));
            } else {
                availabilityLabel.setText("Availability: Not specified");
            }
            descriptionTextArea.setText(accommodation.getDescription());
            // Load images and display them here if needed
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
    }

