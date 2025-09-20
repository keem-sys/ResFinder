package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SavedListingsPanel extends JPanel {

    private final MainWindow mainWindow;
    private final UserService userService;

    // UI Components
    private final JPanel listingGridPanel;
    private final JScrollPane scrollPane;

    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public SavedListingsPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.userService = mainWindow.getUserService();

        // Main Panel Setup
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Top Panel for Title and Navigation
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Panel for the grid of listings
        listingGridPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        listingGridPanel.setOpaque(false);

        scrollPane = new JScrollPane(listingGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Back Button on the left
        JButton backButton = new JButton("< Back to Main View");
        styleButton(backButton, 15);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainWindow.showMainApplicationView());

        JLabel titleLabel = new JLabel("My Saved Listings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        return topPanel;
    }


    public void loadSavedListings() {
        User currentUser = mainWindow.getCurrentUser();
        if (currentUser == null) {
            // Should not happen
            displayMessage("Please log in to view your saved listings.");
            return;
        }

        displayMessage("Loading your saved listings...");

        SwingWorker<List<Accommodation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Accommodation> doInBackground() throws Exception {
                return userService.getSavedListingsForUser(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Accommodation> savedListings = get();
                    refreshListingGrid(savedListings);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    displayMessage("Error loading saved listings. Please try again.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Clears and repopulates the grid with the fetched listings.
     * @param listings The list of accommodations to display.
     */
    private void refreshListingGrid(List<Accommodation> listings) {
        listingGridPanel.removeAll();

        if (listings == null || listings.isEmpty()) {
            displayMessage("You haven't saved any listings yet.");
        } else {
            listingGridPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Accommodation acc : listings) {
                AccommodationCardPanel card = new AccommodationCardPanel(acc, mainWindow);
                listingGridPanel.add(card);
            }
        }

        // Reset scroll pane to top
        SwingUtilities.invokeLater(() -> {
            if (scrollPane != null && scrollPane.getViewport() != null) {
                scrollPane.getViewport().setViewPosition(new Point(0, 0));
            }
        });

        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }


    private void displayMessage(String message) {
        listingGridPanel.removeAll();
        listingGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel infoLabel = new JLabel(message);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        infoLabel.setForeground(TEXT_COLOR);
        listingGridPanel.add(infoLabel);
        listingGridPanel.revalidate();
        listingGridPanel.repaint();
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }

    public JPanel getSavedListingsPanel() {
        return this;
    }
}