package accommodationfinder.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class AboutPanel extends JPanel {

    private final MainWindow mainWindow;

    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);

    public AboutPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Top Panel with Back Button (Unchanged)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JButton backButton = new JButton("< Back to Main View");
        styleButton(backButton, 15);
        backButton.addActionListener(e -> this.mainWindow.showMainApplicationView());
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo + Title
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description
        JTextArea descriptionArea = createDescriptionArea();
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descriptionArea);
        contentPanel.add(Box.createVerticalStrut(20));

        // Development Team Panel
        JPanel teamPanel = createTeamPanel();
        teamPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(teamPanel);

        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Load logo
        ImageIcon logoIcon = loadLogoImage("/images/logo.png", 64, 64);
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            headerPanel.add(logoLabel);
        }

        JLabel titleLabel = new JLabel("ResFinder v1.0");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JTextArea createDescriptionArea() {
        String descriptionText = """
                Your one-stop solution for finding student accommodation
                
                You can browse, search, and filter a curated list of rental properties \
                to find the perfect place to stay.""";

        JTextArea descriptionArea = new JTextArea(descriptionText);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setMaximumSize(new Dimension(600, 200));

        return descriptionArea;
    }

    private JPanel createTeamPanel() {
        JPanel teamPanel = new JPanel(new GridBagLayout());
        teamPanel.setOpaque(false);
        teamPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Development Team",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16), TEXT_COLOR));
        teamPanel.setMaximumSize(new Dimension(600, 400)); // size

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Project Lead
        JEditorPane leadPane = createContributorPane("<b>Lead Developer:</b>", "Ebenezer",
                "https://github.com/keem-sys");
        teamPanel.add(leadPane, gbc);

        // Contributors Title
        gbc.gridy++;
        gbc.insets = new Insets(15, 15, 5, 15);
        JLabel contributorsLabel = new JLabel("Contributors:");
        contributorsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contributorsLabel.setForeground(TEXT_COLOR);
        teamPanel.add(contributorsLabel, gbc);

        // Contributor List
        gbc.gridy++;
        gbc.insets = new Insets(0, 30, 5, 15);
        teamPanel.add(createContributorPane(null, "Matthew", "https://github.com/Matthew-codez"), gbc);
        gbc.gridy++;
        teamPanel.add(createContributorPane(null, "Jayden", "https://github.com/Jaydenchoppa"), gbc);
        gbc.gridy++;
        teamPanel.add(createContributorPane(null, "Wazeer", "https://github.com/WazeerG"), gbc);

        return teamPanel;
    }

    private JEditorPane createContributorPane(String role, String name, String url) {
        String roleHtml = (role != null) ? role + " " : "";
        String html = String.format("<html><body style='font-family: SansSerif; font-size: 15pt;'>"
                        + "%s <a href='%s'>%s</a>"
                        + "</body></html>",
                roleHtml, url, name);

        JEditorPane editorPane = new JEditorPane("text/html", html);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        return editorPane;
    }

    private ImageIcon loadLogoImage(String path, int width, int height) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl == null) {
                System.err.println("Logo not found at: " + path);
                return null;
            }
            BufferedImage originalImage = ImageIO.read(imageUrl);
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }

    public JPanel getAboutPanel() {
        return this;
    }
}