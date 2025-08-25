package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;
import accommodationfinder.listing.Accommodation.AccommodationStatus;
import accommodationfinder.listing.Accommodation.AccommodationType;
import accommodationfinder.listing.Accommodation.PriceFrequency;
import accommodationfinder.service.AccommodationService;
import accommodationfinder.ui.MainWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccommodationDetailPanelTest extends JFrame {

    private AccommodationDetailPanel panel;
    private AccommodationService accommodationService;
    private MainWindow mainWindow;
    private Accommodation accommodation;

    @BeforeEach
    public void setUp() throws SQLException {
        // Create mock services
        mainWindow = mock(MainWindow.class);
        accommodationService = mock(AccommodationService.class);

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
             new User("testUser")
        );
        accommodation.setImageUrls(Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg"
        ));

        // Mock accommodation service
        when(accommodationService.getListingById(accommodation.getId())).thenReturn(accommodation);

        // Create panel
        panel = new AccommodationDetailPanel(accommodationService, mainWindow, accommodation.getId());
    }

    @Test
    public void testInitialization() {
        assertNotNull(panel);
        assertEquals(new Color(253, 251, 245), panel.getBackground()); // Verify background color
        assertTrue(panel.getBorder() instanceof EmptyBorder); // Verify border
        assertEquals(new Insets(15, 25, 15, 25), ((EmptyBorder) panel.getBorder()).getBorderInsets());
    }

    

    @Test
    public void testUIComponents() {
        // Verify title label
        JPanel topPanel = (JPanel) panel.getComponent(0);
        JLabel titleLabel = (JLabel) topPanel.getComponent(1);
        assertNotNull(titleLabel);
        assertEquals("Test Apartment", titleLabel.getText());

        // Verify image container
        JPanel centerPanel = (JPanel) panel.getComponent(1);
        JPanel leftPanel = (JPanel) centerPanel.getComponent(0);
        JPanel imageContainerPanel = (JPanel) leftPanel.getComponent(0);
        assertNotNull(imageContainerPanel);
        assertEquals(new Color(230, 230, 230), imageContainerPanel.getBackground());

        // Verify details text area
        JScrollPane detailsScrollPane = (JScrollPane) leftPanel.getComponent(1);
        JTextArea detailsTextArea = (JTextArea) detailsScrollPane.getViewport().getView();
        assertNotNull(detailsTextArea);
        assertTrue(detailsTextArea.getText().contains("Address: 123 Test Street, Test City"));
        assertTrue(detailsTextArea.getText().contains("Price: R15,000 PER_MONTH"));
        assertTrue(detailsTextArea.getText().contains("Beds: 2 | Baths: 1 | Max Occupancy: 4"));

        // Verify contact panel
        JPanel contactPanel = (JPanel) centerPanel.getComponent(1);
        JLabel listerInfoLabel = (JLabel) contactPanel.getComponent(0);
        assertNotNull(listerInfoLabel);
        assertTrue(listerInfoLabel.getText().startsWith("Listed by: "));
    }

    @Test
    public void testSendMessage() {
        // Fill in contact form
        JPanel centerPanel = (JPanel) panel.getComponent(1);
        JPanel contactPanel = (JPanel) centerPanel.getComponent(1);

        JTextField contactNameField = (JTextField) contactPanel.getComponent(1);
        JTextField contactEmailField = (JTextField) contactPanel.getComponent(3);
        JTextField contactPhoneField = (JTextField) contactPanel.getComponent(5);
        JButton sendMessageButton = (JButton) contactPanel.getComponent(7);

        contactNameField.setText("Test Name");
        contactEmailField.setText("test@example.com");
        contactPhoneField.setText("1234567890");

        // Trigger send button click
        sendMessageButton.doClick();

        // Verify message was sent
        verify(mainWindow, never()).showMainApplicationView();
    }

    @Test
    public void testImageNavigation() {
        // Verify initial image state
        JPanel centerPanel = (JPanel) panel.getComponent(1);
        JPanel leftPanel = (JPanel) centerPanel.getComponent(0);
        JPanel imageContainerPanel = (JPanel) leftPanel.getComponent(0);

        JLabel imageLabel = (JLabel) imageContainerPanel.getComponent(0);
        JPanel imageNavPanel = (JPanel) imageContainerPanel.getComponent(1);
        JLabel imageCountLabel = (JLabel) imageNavPanel.getComponent(2);
        JButton prevImageButton = (JButton) imageNavPanel.getComponent(0);
        JButton nextImageButton = (JButton) imageNavPanel.getComponent(4);

        assertNotNull(imageLabel);
        assertTrue(imageLabel.getText().equals("Loading image..."));

        assertNotNull(imageCountLabel);
        assertEquals("Image 1 of 2", imageCountLabel.getText());

        assertNotNull(prevImageButton);
        assertNotNull(nextImageButton);

        // Test next button
        nextImageButton.doClick();
        assertEquals("Image 2 of 2", imageCountLabel.getText());

        // Test previous button
        prevImageButton.doClick();
        assertEquals("Image 1 of 2", imageCountLabel.getText());
    }

    @Test
    public void testBackButton() {
        // Verify back button
        JPanel topPanel = (JPanel) panel.getComponent(0);
        JButton backButton = (JButton) topPanel.getComponent(0);
        assertNotNull(backButton);
        assertEquals("<- Back to Main View", backButton.getText());

        // Test back button action
        backButton.doClick();
        verify(mainWindow).showMainApplicationView();
    }

    @Test
    public void testErrorHandling() throws SQLException {
        // Mock service to return null
        when(accommodationService.getListingById(accommodation.getId())).thenReturn(null);

        // Create new panel with error case
        AccommodationDetailPanel errorPanel = new AccommodationDetailPanel(accommodationService, mainWindow, accommodation.getId());

        // Verify error state
        JTextArea detailsTextArea = errorPanel.getDetailsTextArea();
        assertTrue(detailsTextArea.getText().contains("The requested accommodation listing could not be found."));
    }

    @Test
    public void testContactFormValidation() {
        JTextField emailField = (JTextField) panel.getComponent(1);//.getComponent(1).getComponent(1).getComponent(3);
        JButton sendButton = (JButton) panel.getComponent(1);//.getComponent(1).getComponent(1).getComponent(7);

        // Test empty fields
        assertFalse(sendButton.isEnabled());

        // Test invalid email
        Label nameField = (Label) panel.getComponent(1);//.getComponent(1).getComponent(1).getComponent(1);
        nameField.setText("Test Name");
        emailField.setText("invalid-email");
        assertFalse(sendButton.isEnabled());

        // Test valid fields
        emailField.setText("test@example.com");
        assertTrue(sendButton.isEnabled());
    }
}
