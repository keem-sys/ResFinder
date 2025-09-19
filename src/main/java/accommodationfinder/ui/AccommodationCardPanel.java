package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation; // Import Accommodation class
import accommodationfinder.service.UserService;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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
    private final UserService userService;
    private JButton saveButton;
    private ImageIcon heartEmptyIcon;
    private ImageIcon heartFilledIcon;
    private boolean isSaved = false;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new
            Locale("en", "ZA"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color FIELD_COLOR = new Color(0, 100, 200);
    private static final Color IMAGE_LABEL_BACKGROUND_COLOR = new Color(230, 230, 230);

    public AccommodationCardPanel(Accommodation accommodation, MainWindow mainWindow) {
        super(new BorderLayout(0, 0));
        this.accommodation = accommodation;
        this.mainWindow = mainWindow;
        this.userService = mainWindow.getUserService();
        loadIcons();
        initComponents();
        setInitialSaveState();
    }

    private void loadIcons() {
        heartEmptyIcon = loadIcon("/icons/heart_empty.png", 24, 24);
        heartFilledIcon = loadIcon("/icons/heart_filled.png", 24, 24);
    }

    private void initComponents() {
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(BACKGROUND_COLOR);

        // Image Panel (Top)
        JPanel imageContainerPanel = createImagePanel();
        add(imageContainerPanel, BorderLayout.NORTH);

        // Details Panel (Center)
        JPanel detailsPanel = createDetailsPanel();
        add(detailsPanel, BorderLayout.CENTER);

        // Bottom Bar (South)
        JPanel bottomBarPanel = createBottomBar();
        add(bottomBarPanel, BorderLayout.SOUTH);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getSource() == saveButton) {
                    return;
                }
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
        imageLabel.setBackground(IMAGE_LABEL_BACKGROUND_COLOR);
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
        detailsPanel.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(limitString(accommodation.getTitle(), 45));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        saveButton = new JButton();
        styleIconButton(saveButton);
        updateSaveButtonIcon(); // Set initial icon
        saveButton.addActionListener(e -> handleSaveButtonClick());
        titlePanel.add(saveButton, BorderLayout.EAST);

        JLabel priceLabel = new JLabel(currencyFormatter.format(accommodation.getPrice()) + " " + formatPriceFrequency(accommodation.getPriceFrequency()));
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
        nsfasLabel.setForeground(accommodation.isNsfasAccredited() ? FIELD_COLOR : Color.GRAY);
        nsfasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nsfasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(titlePanel);

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
        bottomBarPanel.setBackground(IMAGE_LABEL_BACKGROUND_COLOR);
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
        viewMoreLabel.setForeground(FIELD_COLOR);
        viewMoreLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        bottomBarPanel.add(availableLabel, BorderLayout.WEST);
        bottomBarPanel.add(viewMoreLabel, BorderLayout.EAST);

        return bottomBarPanel;
    }

    private void handleCardClick() {
        System.out.println("Card clicked - Navigate to details for Listing ID: " + accommodation.getId());
        mainWindow.switchToDetailedView(accommodation.getId());
    }

    private void setInitialSaveState() {
        User currentUser = mainWindow.getCurrentUser();
        if (currentUser == null) {
            this.isSaved = false;
            updateSaveButtonIcon();
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userService.isListingSaved(currentUser.getId(), accommodation.getId());
            }

            @Override
            protected void done() {
                try {
                    isSaved = get();
                    updateSaveButtonIcon();
                } catch (Exception e) {
                    System.err.println("Error checking initial saved state: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleSaveButtonClick() {
        User currentUser = mainWindow.getCurrentUser();

        // Check if user logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainWindow, "Please log in to save listings.", "Login Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Perform database action in the background
        saveButton.setEnabled(false);
        final boolean saving = !isSaved;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (saving) {
                    userService.addSavedListing(currentUser.getId(), accommodation.getId());
                } else {
                    userService.removeSavedListing(currentUser.getId(), accommodation.getId());
                }
                return saving;
            }

            @Override
            protected void done() {
                try {
                    isSaved = get();
                    updateSaveButtonIcon();
                } catch (Exception e) {
                    System.err.println("Error toggling saved state: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainWindow, "Could not update " +
                            "saved status. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    saveButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void updateSaveButtonIcon() {
        if (isSaved) {
            saveButton.setIcon(heartFilledIcon);
            saveButton.setToolTipText("Remove from Saved Listings");
        } else {
            saveButton.setIcon(heartEmptyIcon);
            saveButton.setToolTipText("Save this Listing");
        }
    }

    private void styleIconButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Helper Methods

    private void loadImageAsync(JLabel imageLabel, int targetWidth, int targetHeight) {
        // Reset label state
        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");

        if (accommodation.getImageUrls() != null && !accommodation.getImageUrls().isEmpty() &&
                accommodation.getImageUrls().get(0) != null && !accommodation.getImageUrls().get(0).trim().isEmpty()) {

            String imageUrlString = accommodation.getImageUrls().get(0).trim();

            SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() {
                    Image scaledImage = null;

                    try {
                        URL imageUrl = new URL(imageUrlString.trim());
                        BufferedImage originalImage = null;
                        try (InputStream is = imageUrl.openStream()) {
                            originalImage = ImageIO.read(is);
                        }

                        // Check if ImageIO successfully read the image
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

                        // Ensure minimum dimensions after scaling if needed
                        scaledWidth = Math.max(1, scaledWidth);
                        scaledHeight = Math.max(1, scaledHeight);


                        // Create a BufferedImage for higher quality scaling
                        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = scaledBI.createGraphics();

                        // Apply rendering hints for better quality
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Draw the original image onto BufferedImage
                        g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
                        g2d.dispose();

                        return new ImageIcon(scaledBI);

                    } catch (MalformedURLException e) {
                        System.err.println("Invalid image URL: " + imageUrlString + " - " + e.getMessage());
                        return null;
                    } catch (IIOException e) {
                        System.err.println("ImageIO error loading/reading image: " + imageUrlString + " - " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    } catch (IOException e) {
                        System.err.println("IO error loading image stream: " + imageUrlString + " - " + e.getMessage());
                        e.printStackTrace();
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


    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Could not find icon resource: " + path);
                return null;
            }
            BufferedImage img = ImageIO.read(url);
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            System.err.println("Error loading icon: " + path);
            e.printStackTrace();
            return null;
        }
    }
}