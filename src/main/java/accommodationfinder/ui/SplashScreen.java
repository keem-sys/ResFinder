package accommodationfinder.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(253, 251, 245));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        titlePanel.setOpaque(false);

        ImageIcon logoIcon = loadLogoImage("/images/logo.png", 64, 64);
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            titlePanel.add(logoLabel);
        }

        JLabel textLabel = new JLabel("ResFinder");
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titlePanel.add(textLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        contentPanel.add(titlePanel, gbc);


        JLabel statusLabel = new JLabel("Initializing application...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        contentPanel.add(statusLabel, gbc);


        setContentPane(contentPanel);

        setSize(700, 400);
        setLocationRelativeTo(null);
    }

    private ImageIcon loadLogoImage(String path, int width, int height) {
        try {
            URL imageURL = getClass().getResource(path);
            if (imageURL == null) {
                System.err.println("Splash screen logo not found at: " + path);
                return null;
            }
            BufferedImage originalImage = ImageIO.read(imageURL);
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error loading splash screen logo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}