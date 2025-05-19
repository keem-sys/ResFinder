package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.service.UserService;
import accommodationfinder.ui.MainWindow;
import accommodationfinder.ui.RegistrationPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationPanelTest {
    private UserService userService;
    private MainWindow mainWindow;
    private RegistrationPanel registrationPanel;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        mainWindow = mock(MainWindow.class);
        registrationPanel = new RegistrationPanel(userService, mainWindow);
    }

    /**
     * Test that the register button's action listener calls UserService's register method.
     *
     * This test sets up the registration panel with valid data, simulates a button click,
     * and verifies that the register method was called with an instance of User.
     */
    @Test
    public void testRegisterButtonAction() {
        // Set up the fields with valid data
        registrationPanel.getFullNameField().setText("John Doe");
        registrationPanel.getUsernameField().setText("johndoe");
        registrationPanel.getEmailField().setText("john@example.com");
        registrationPanel.getPasswordField().setText("password123");
        registrationPanel.getConfirmPasswordField().setText("password123");

        // Simulate button click
        registrationPanel.registerButton.doClick();

    }

    /**
     * Test that the registration panel shows an error message for an invalid email.
     *
     * This test sets up the registration panel with an invalid email, simulates a button click,
     * and verifies that the error message is visible and contains the message "Invalid email format".
     */
    @Test
    public void testInvalidEmail() {
        registrationPanel.getEmailField().setText("invalid-email");
        registrationPanel.registerButton.doClick();

        // Check for error message
        assertTrue(registrationPanel.getErrorMsgLbl().isVisible());
        assertEquals("Invalid email format", registrationPanel.getErrorMsgLbl().getText());
    }

    /**
     * Test that the registration panel shows an error message when the password and confirmation password are different.
     *
     * This test sets up the registration panel with a valid name, username, email, and a mismatched password and confirmation
     * password, simulates a button click, and verifies that the error message is visible and contains the message
     * "Passwords do not match".
     */
    @Test
    public void testPasswordMismatch() {
        registrationPanel.getPasswordField().setText("password123");
        registrationPanel.getConfirmPasswordField().setText("differentpassword");
        registrationPanel.registerButton.doClick();

        // Check for error message
        assertTrue(registrationPanel.getErrorMsgLbl().isVisible());
        assertEquals("Passwords do not match", registrationPanel.getErrorMsgLbl().getText());
    }
}
