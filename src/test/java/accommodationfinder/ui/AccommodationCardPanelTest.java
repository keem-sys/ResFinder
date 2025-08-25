package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;
import accommodationfinder.listing.Accommodation.AccommodationStatus;
import accommodationfinder.listing.Accommodation.AccommodationType;
import accommodationfinder.listing.Accommodation.PriceFrequency;
import accommodationfinder.ui.MainWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccommodationCardPanelTest {

    private AccommodationCardPanel panel;
    private Accommodation accommodation;
    private MainWindow mainWindow;

    @BeforeEach
    public void setUp() {
        // Create mock MainWindow
        mainWindow = Mockito.mock(MainWindow.class);

        // Create test accommodation
        accommodation = new Accommodation(
            "Test Apartment",
            "A nice apartment for students",
            AccommodationType.APARTMENT,
            "123 Test Street",
            "Test City",
            "12345",
            -26.2041,
            28.0473,
            new BigDecimal("15000"),
            PriceFrequency.PER_MONTH,
            2,
            1,
            4,
            true,
            true,
            true,
            "12-month lease",
            LocalDateTime.now(),
            LocalDateTime.now().plusYears(1),
            true,
            new User()
        );

        // Create panel with mocks
        panel = new AccommodationCardPanel(accommodation, mainWindow);
    }

    @Test
    public void testInitialization() {
        assertNotNull(panel);
        assertEquals(new Color(253, 251, 245), panel.getBackground()); // Verify background color
        assertTrue(panel.getBorder() instanceof javax.swing.border.LineBorder); // Verify border
        assertEquals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), panel.getCursor()); // Verify cursor
    }

    @Test
    public void testClickHandling() {
        // Simulate mouse click
        MouseEvent evt = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 10, 10, 1, false);
        panel.dispatchEvent(evt);

        // Verify that switchToDetailedView was called with the correct ID
        verify(mainWindow).switchToDetailedView(accommodation.getId());
    }

    @Test
    public void testImagePanel() {
        Component[] components = panel.getComponents();
        JPanel imagePanel = (JPanel) components[0]; // First component should be image panel
        assertNotNull(imagePanel);
        assertEquals(new Color(220, 220, 220), imagePanel.getBackground());

        // Verify image label properties
        JLabel imageLabel = (JLabel) imagePanel.getComponent(0);
        assertNotNull(imageLabel);
        assertEquals(new Dimension(500, 300), imageLabel.getPreferredSize());
        assertTrue(imageLabel.getText().equals("Loading..."));
    }

    @Test
    public void testDetailsPanel() {
        Component[] components = panel.getComponents();
        JPanel detailsPanel = (JPanel) components[1]; // Second component should be details panel
        assertNotNull(detailsPanel);
        assertEquals(new Color(253, 251, 245), detailsPanel.getBackground());

        // Verify title label
        JLabel titleLabel = (JLabel) detailsPanel.getComponent(0);
        assertNotNull(titleLabel);
        assertEquals(new Font("Arial", Font.BOLD, 16), titleLabel.getFont());
        assertTrue(titleLabel.getText().equals("Test Apartment"));

        // Verify price label
        JLabel priceLabel = (JLabel) detailsPanel.getComponent(2);
        assertNotNull(priceLabel);
        assertEquals(new Font("Arial", Font.PLAIN, 14), priceLabel.getFont());
        assertTrue(priceLabel.getText().contains("R15,000"));

        // Verify info label
        JLabel infoLabel = (JLabel) detailsPanel.getComponent(4);
        assertNotNull(infoLabel);
        assertEquals(new Font("Arial", Font.PLAIN, 13), infoLabel.getFont());
        assertEquals(Color.DARK_GRAY, infoLabel.getForeground());
        assertTrue(infoLabel.getText().equals("2 Bed | 1 Bath | Test City"));

        // Verify NSFAS label
        JLabel nsfasLabel = (JLabel) detailsPanel.getComponent(6);
        assertNotNull(nsfasLabel);
        assertEquals(new Font("Arial", Font.PLAIN, 12), nsfasLabel.getFont());
        assertEquals(new Color(0, 100, 200), nsfasLabel.getForeground());
        assertTrue(nsfasLabel.getText().equals("NSFAS Accredited"));
    }

    @Test
    public void testBottomBar() {
        Component[] components = panel.getComponents();
        JPanel bottomBar = (JPanel) components[2]; // Third component should be bottom bar
        assertNotNull(bottomBar);
        assertEquals(new Color(230, 230, 230), bottomBar.getBackground());

        // Verify availability label
        JLabel availableLabel = (JLabel) bottomBar.getComponent(0);
        assertNotNull(availableLabel);
        assertEquals(new Font("Arial", Font.PLAIN, 12), availableLabel.getFont());
        assertEquals(Color.DARK_GRAY, availableLabel.getForeground());
        assertTrue(availableLabel.getText().startsWith("Available:"));

        // Verify view more label
        JLabel viewMoreLabel = (JLabel) bottomBar.getComponent(1);
        assertNotNull(viewMoreLabel);
        assertEquals(new Font("Arial", Font.BOLD, 12), viewMoreLabel.getFont());
        assertEquals(new Color(0, 100, 200), viewMoreLabel.getForeground());
        assertEquals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), viewMoreLabel.getCursor());
        assertTrue(viewMoreLabel.getText().equals("View More"));
    }

    @Test
    public void testTitleTruncation() {
        // Create accommodation with long title
        Accommodation longTitleAccommodation = new Accommodation(
            "This is a very very very very very very very very very long title that should be truncated",
            "Description",
            AccommodationType.APARTMENT,
            "Address",
            "City",
            "12345",
            0.0,
            0.0,
            BigDecimal.ONE,
            PriceFrequency.PER_MONTH,
            1,
            1,
            1,
            true,
            true,
            true,
            "Lease",
            LocalDateTime.now(),
            LocalDateTime.now(),
            true,
            new User()
        );

        // Create panel with long title accommodation
        AccommodationCardPanel longTitlePanel = new AccommodationCardPanel(longTitleAccommodation, mainWindow);
        Component[] components = longTitlePanel.getComponents();
        JPanel detailsPanel = (JPanel) components[1];
        JLabel titleLabel = (JLabel) detailsPanel.getComponent(0);

        // Verify title is truncated
        assertTrue(titleLabel.getText().length() <= 45 + 3); // +3 for ellipsis
        assertTrue(titleLabel.getText().endsWith("..."));
    }
}
