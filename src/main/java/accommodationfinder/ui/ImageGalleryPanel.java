package accommodationfinder.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ImageGalleryPanel extends JPanel {
    private JLabel imageLabel;
    private JButton prevImageButton, nextImageButton;
    private JLabel imageCountLabel;

    private List<String> currentImageUrls;
    private int currentImageIndex = 0;

    private final int IMG_WIDTH;
    private final int IMG_HEIGHT;

    // Style Constants
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color PLACEHOLDER_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public ImageGalleryPanel(int width, int height) {
        this.IMG_WIDTH = width;
        this.IMG_HEIGHT = height;

        setLayout(new BorderLayout());
        setBackground(PLACEHOLDER_COLOR);
        setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT + 70));

        initComponents();
        updateNavigationState();
    }

    private void initComponents() {
        imageLabel = new JLabel("No images available", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(PLACEHOLDER_COLOR);
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        add(imageLabel, BorderLayout.CENTER);

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
        add(imageNavPanel, BorderLayout.SOUTH);
    }

    /**
     * Public method to load images into the gallery.
     * This is called by the parent panel after it fetches the data.
     *
     * @param imageUrls The list of image URLs to display.
     */
    public void setImageUrls(List<String> imageUrls) {
        this.currentImageUrls = imageUrls;
        this.currentImageIndex = 0;
        showImageAtIndex(0);
    }

    private void showImageAtIndex(int index) {
        if (currentImageUrls == null || currentImageUrls.isEmpty()) {
            updateNavigationState();
            return;
        }

        if (index < 0 || index >= currentImageUrls.size()) {
            return; // Invalid index, do nothing
        }

        currentImageIndex = index;
        loadImageAsync(currentImageUrls.get(index));
        updateNavigationState();
    }

    private void updateNavigationState() {
        if (currentImageUrls == null || currentImageUrls.isEmpty()) {
            imageLabel.setText("No Images Available");
            imageLabel.setIcon(null);
            imageCountLabel.setText("Image 0 of 0");
            prevImageButton.setEnabled(false);
            nextImageButton.setEnabled(false);
        } else {
            int totalImages = currentImageUrls.size();
            imageCountLabel.setText("Image " + (currentImageIndex + 1) + " of " + totalImages);
            prevImageButton.setEnabled(currentImageIndex > 0);
            nextImageButton.setEnabled(currentImageIndex < totalImages - 1);
        }
    }

    private void loadImageAsync(String imageUrlString) {
        imageLabel.setIcon(null);
        imageLabel.setText("Loading image...");

        if (imageUrlString == null || imageUrlString.trim().isEmpty()) {
            imageLabel.setText("No Image Available");
            return;
        }

        SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL imageUrl = new URL(imageUrlString.trim());
                BufferedImage originalImage;
                try (InputStream is = imageUrl.openStream()) {
                    originalImage = ImageIO.read(is);
                }

                if (originalImage == null) throw new IOException("Could not decode image");

                double scale = Math.min((double) IMG_WIDTH / originalImage.getWidth(), (double) IMG_HEIGHT / originalImage.getHeight());
                int scaledWidth = (int) (originalImage.getWidth() * scale);
                int scaledHeight = (int) (originalImage.getHeight() * scale);

                BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledBI.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
                g2d.dispose();
                return new ImageIcon(scaledBI);
            }

            @Override
            protected void done() {
                try {
                    ImageIcon scaledIcon = get();
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText(null);
                    imageLabel.setBackground(BACKGROUND_COLOR);
                } catch (Exception e) {
                    imageLabel.setText("Image Unavailable");
                    imageLabel.setIcon(null);
                    imageLabel.setBackground(PLACEHOLDER_COLOR);
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
        };
        imageLoader.execute();
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }
}

