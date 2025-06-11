package accommodationfinder.ui;

import accommodationfinder.service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;


public class LoginPanel extends JPanel {

    private JLabel usernameOrEmailLabel, passwordLabel, errorMessageLabel, titleLabel, registerPromptLabel;
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton, registrationButton; // Changed cancelButton to backButton
    private JCheckBox rememberMeChkBox;
    private final UserService userService;
    private final MainWindow mainWindow;

    // Define Colors based on Wireframe
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245); // Beige background
    private static final Color FIELD_BACKGROUND_COLOR = new Color(230, 230, 230); // Light gray for fields/buttons
    private static final Color TEXT_COLOR = new Color(50, 50, 50); // Dark gray for text
    private static final Color ERROR_COLOR = Color.RED;

    public LoginPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        // Main Panel Setup
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Top Bar (Back Button)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        backButton = new JButton("<- Back to Main View");

        styleButton(backButton, FIELD_BACKGROUND_COLOR, TEXT_COLOR, 13);
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Form Area
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Transparent background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        titleLabel = new JLabel("Welcome back!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 30, 8);
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1; // Reset
        gbc.insets = new Insets(8, 8, 8, 8); // Reset insets

        // Username/Email Row
        usernameOrEmailLabel = new JLabel("Username or Email:");
        styleLabel(usernameOrEmailLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameOrEmailLabel, gbc);

        usernameOrEmailField = new JTextField(25);
        styleTextField(usernameOrEmailField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameOrEmailField, gbc);

        // Password Row
        passwordLabel = new JLabel("Password:");
        styleLabel(passwordLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        // Remember Me Checkbox
        rememberMeChkBox = new JCheckBox("Remember Me");
        rememberMeChkBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rememberMeChkBox.setForeground(TEXT_COLOR);
        rememberMeChkBox.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 15, 8);
        formPanel.add(rememberMeChkBox, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);

        // Error Message Label
        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(ERROR_COLOR);
        errorMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 8, 8, 8);
        formPanel.add(errorMessageLabel, gbc);
        gbc.gridwidth = 1; // Reset
        gbc.insets = new Insets(8, 8, 8, 8); // Reset


        // Log In Button (Centered)
        loginButton = new JButton("Log In");
        styleButton(loginButton, FIELD_BACKGROUND_COLOR, TEXT_COLOR, 15);
        loginButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 8, 30, 8);
        formPanel.add(loginButton, gbc);
        gbc.gridwidth = 1; // Reset
        gbc.insets = new Insets(8, 8, 8, 8); // Reset

        // Sign Up Prompt
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0)); // Centered flow layout
        registerPanel.setOpaque(false);
        registerPromptLabel = new JLabel("Don't have an account?");
        styleLabel(registerPromptLabel);
        registrationButton = new JButton("Sign Up!");
        styleButton(registrationButton, FIELD_BACKGROUND_COLOR, TEXT_COLOR, 13); // Style sign up button
        registerPanel.add(registerPromptLabel);
        registerPanel.add(registrationButton);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerPanel, gbc);


        // Add formPanel to the centering wrapper
        centerWrapper.add(formPanel, new GridBagConstraints());
        // Add the centering wrapper to the main panel's center
        add(centerWrapper, BorderLayout.CENTER);


        // Login Button Action
        loginButton.addActionListener(e -> performLogin());

        // Password Field Action
        passwordField.addActionListener(e -> performLogin());

        // Back Button Action
        backButton.addActionListener(e -> {
            System.out.println("Back button clicked - Switching to Main View");
            clearInputs();
            mainWindow.showMainApplicationView();
        });

        // Registration Button Action
        registrationButton.addActionListener(e -> {
            System.out.println("Sign Up button clicked on Login Panel - Switching to Registration Panel");
            clearInputs();
            SwingUtilities.invokeLater(mainWindow::switchToRegistrationPanel);
        });

    }


    /**
     * Attempts the login process using the UserService.
     * Handles UI updates for success and failure.
     */
    private void performLogin() {
        System.out.println("Login action triggered");
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        boolean rememberMe = rememberMeChkBox.isSelected();

        System.out.println("Attempting login for: " + usernameOrEmail);
        System.out.println("Remember me: " + rememberMe);

        // Basic validation
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            setErrorMessage("Username/Email and Password are required");
            Arrays.fill(passwordChars, ' '); // Clear password array
            return;
        } else {
            setErrorMessage(" "); // Clear previous error message
        }

        try {
            // Call UserService to attempt login
            String jwtToken = userService.loginUser(usernameOrEmail, password);
            // Login Successful
            System.out.println("Login attempt successful via UserService. Token received.");

            // Handle 'Remember Me'
            if (rememberMe) {
                mainWindow.saveJwtToPreferences(jwtToken);
                System.out.println("JWT token marked for saving.");
            } else {
                mainWindow.saveJwtToPreferences(null);
                System.out.println("JWT token marked for clearing.");
            }

            // Notify MainWindow of successful login
            mainWindow.handleLoginSuccess(jwtToken, true);

            // Clear input fields on success
            clearInputs();

        } catch (Exception authenticationException) {
            // Login Failed
            System.err.println("Login failed: " + authenticationException.getMessage());
            // Provide error message
            setErrorMessage("Login failed: Invalid username/email or password.");
            passwordField.setText("");
            passwordField.requestFocusInWindow();

        } finally {
            // Clear password array
            Arrays.fill(passwordChars, ' ');
        }
    }

    /**
     * Clears all input fields and the error message.
     */
    private void clearInputs() {
        usernameOrEmailField.setText("");
        passwordField.setText("");
        rememberMeChkBox.setSelected(false);
        setErrorMessage(" ");
    }

    /**
     * Sets the text of the error message label.
     * @param message The message to display, or " " to clear.
     */
    public void setErrorMessage(String message) {
        errorMessageLabel.setText(message == null ? " " : message);
    }

    /**
     * Helper to style JLabels consistently.
     */
    private void styleLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
    }

    /**
     * Helper to style JTextField and JPasswordField consistently.
     */
    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(FIELD_BACKGROUND_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND_COLOR.darker(), 1),
                new EmptyBorder(5, 8, 5, 8) // Internal padding
        ));
        field.setCaretColor(TEXT_COLOR);
    }

    /**
     * Helper to style JButtons consistently.
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        // TODO: Add hover effect listener
    }

    /**
     * Requests that the initial focus be set on the username/email field
     * for this panel. Should be called after the panel is visible or
     * added to the main window.
     */
    public void requestInitialFocus() {
        SwingUtilities.invokeLater(() -> {
            if (usernameOrEmailField != null) {
                usernameOrEmailField.requestFocusInWindow();
            }
        });
    }

    // Method needed by MainWindow to switch panels
    public JPanel getLoginPanel() {
        return this;
    }
}