package accommodationfinder.ui;

import accommodationfinder.listing.Accommodation;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A JPanel that displays a single accommodation listing in a card format.
 */
public class AccommodationCardPanel extends JPanel {

    private final Accommodation accommodation;
    private final MainWindow mainWindow;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");


    public AccommodationCardPanel(Accommodation accommodation, MainWindow mainWindow) {
        super(new BorderLayout(0, 0));
        this.accommodation = accommodation;
        this.mainWindow = mainWindow;


        initComponents();
    }

    private void initComponents() {
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(new Color(253, 251, 245)); // Match background

        // Image Panel (Top)
        JPanel imageContainerPanel = createImagePanel();
        add(imageContainerPanel, BorderLayout.NORTH);

        // Details Panel (Center)
        JPanel detailsPanel = createDetailsPanel();
        add(detailsPanel, BorderLayout.CENTER);

        // Bottom Bar (South)
        JPanel bottomBarPanel = createBottomBar();
        add(bottomBarPanel, BorderLayout.SOUTH);

        // Click Listener for the whole card
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleCardClick();
            }
        });
    }

    private JPanel createImagePanel() {
        final int IMG_WIDTH = 500;
        final int IMG_HEIGHT = 300;
        JPanel imageContainerPanel = new JPanel(new BorderLayout());
        imageContainerPanel.setBackground(new Color(220, 220, 220));

        JLabel imageLabel = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(230, 230, 230));
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageContainerPanel.add(imageLabel, BorderLayout.CENTER);

        // Load Image using SwingWorker
        loadImageAsync(imageLabel, IMG_WIDTH, IMG_HEIGHT);

        return imageContainerPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        detailsPanel.setBackground(new Color(253, 251, 245)); // Match background

        JLabel titleLabel = new JLabel(limitString(accommodation.getTitle(), 45));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(currencyFormatter.format(accommodation.getPrice()) + " " +
                formatPriceFrequency(accommodation.getPriceFrequency()));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(String.format("%d Bed | %d Bath | %s",
                accommodation.getBedrooms(),
                accommodation.getBathrooms(),
                limitString(accommodation.getCity(), 20)));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(Color.DARK_GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nsfasLabel = new JLabel("NSFAS " + (accommodation.isNsfasAccredited() ? "Accredited" : "Not Accredited"));
        nsfasLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nsfasLabel.setForeground(accommodation.isNsfasAccredited() ? new Color(0, 100, 200) : Color.GRAY);
        nsfasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(infoLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(nsfasLabel);

        return detailsPanel;
    }

    private JPanel createBottomBar() {
        JPanel bottomBarPanel = new JPanel(new BorderLayout(10, 0));
        bottomBarPanel.setBackground(new Color(230, 230, 230));
        bottomBarPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(6, 12, 6, 12)
        ));
        bottomBarPanel.setOpaque(true);

        // Availability Label (Left)
        String availableText = "Available: ";
        if (accommodation.getAvailableFrom() != null) {
            if (accommodation.getAvailableFrom().isAfter(LocalDateTime.now())) {
                availableText += accommodation.getAvailableFrom().format(dateFormatter);
            } else {
                availableText += "Now";
            }
        } else {
            availableText += "-";
        }
        JLabel availableLabel = new JLabel(availableText);
        availableLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        availableLabel.setForeground(Color.DARK_GRAY);

        // View More Label (Right)
        JLabel viewMoreLabel = new JLabel("View More");
        viewMoreLabel.setFont(new Font("Arial", Font.BOLD, 12));
        viewMoreLabel.setForeground(new Color(0, 100, 200));
        viewMoreLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        bottomBarPanel.add(availableLabel, BorderLayout.WEST);
        bottomBarPanel.add(viewMoreLabel, BorderLayout.EAST);

        return bottomBarPanel;
    }

    private void handleCardClick() {
        // TODO: trigger navigation to the detailed view
        System.out.println("Card clicked - Navigate to details for Listing ID: " + accommodation.getId());
        // Placeholder action using mainWindow reference
        JOptionPane.showMessageDialog(null, // Use mainWindow's frame
                "Should show details for: " + accommodation.getTitle(),
                "View More Clicked (Placeholder)",
                JOptionPane.INFORMATION_MESSAGE);

        // TODO: mainWindow.switchToDetailedView(accommodation.getId());
    }

    // Helper Methods specific to this card

    private void loadImageAsync(JLabel imageLabel, int targetWidth, int targetHeight) {
        // Reset label state
        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");

        if (accommodation.getImageUrls() != null && !accommodation.getImageUrls().isEmpty() &&
                accommodation.getImageUrls().get(0) != null && !accommodation.getImageUrls().get(0).trim().isEmpty()) {

            String imageUrlString = accommodation.getImageUrls().get(0).trim();

            SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    try {
                        URL imageUrl = new URL(imageUrlString);
                        ImageIcon originalIcon = new ImageIcon(imageUrl);

                        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                            System.err.println("Failed to load image: " + imageUrlString + " (Status: " + originalIcon.getImageLoadStatus()+")");
                            return null;
                        }

                        Image image = originalIcon.getImage();
                        Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);

                    } catch (MalformedURLException e) {
                        System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    } catch (Exception e) {
                        System.err.println("Error loading/scaling image: " + imageUrlString + " - " + e.getMessage());
                        // e.printStackTrace();
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
                        } else {
                            imageLabel.setText("No Image");
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating image label: " + e.getMessage());
                        e.printStackTrace();
                        imageLabel.setText("Error");
                        imageLabel.setIcon(null);
                    }
                }
            };
            imageLoader.execute();
        } else {
            imageLabel.setText("No Image");
        }
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

    private String limitString(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) {
            return text;
        } else {
            int lastSpace = text.lastIndexOf(' ', maxLength - 3);
            if (lastSpace > maxLength / 2) {
                return text.substring(0, lastSpace) + "...";
            } else {
                return text.substring(0, maxLength - 3) + "...";
            }
        }
    }
}