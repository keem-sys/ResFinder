package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {

    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton createAccountButton, backButton, loginButton;
    private JCheckBox termsCheckBox;
    private JLabel errorMsgLbl, titleLabel, loginPromptLabel;
    private final UserService userService;
    private final MainWindow mainWindow;

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color ERROR_COLOR = new Color(211, 47, 47);

    public RegistrationPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        // Main Panel Setup
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 25, 15, 25)); // Padding

        // Back Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        backButton = new JButton("<- Back to Main View");
        styleButton(backButton, 15);
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Form Area
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        titleLabel = new JLabel("Create an account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 30, 8);
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Full Name Row
        JLabel fullNameLabel = new JLabel("Full Name:");
        styleLabel(fullNameLabel);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(fullNameLabel, gbc);

        fullNameField = new JTextField(25);
        styleTextField(fullNameField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(fullNameField, gbc);

        // Username Row
        JLabel usernameLabel = new JLabel("Username:");
        styleLabel(usernameLabel);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(25);
        styleTextField(usernameField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);

        // Email Row
        JLabel emailLabel = new JLabel("Email:");
        styleLabel(emailLabel);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(25);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);

        // Password Row
        JLabel passwordLabel = new JLabel("Password:");
        styleLabel(passwordLabel);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        // Confirm Password Row
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        styleLabel(confirmPasswordLabel);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(25);
        styleTextField(confirmPasswordField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(confirmPasswordField, gbc);

        // Terms Checkbox
        termsCheckBox = new JCheckBox("I agree to the Terms and Services");
        termsCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        termsCheckBox.setForeground(TEXT_COLOR);
        termsCheckBox.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 15, 8);
        formPanel.add(termsCheckBox, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);

        // Error Message Label
        errorMsgLbl = new JLabel(" ");
        errorMsgLbl.setForeground(ERROR_COLOR);
        errorMsgLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorMsgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 8, 8, 8);
        formPanel.add(errorMsgLbl, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Create Account Button
        createAccountButton = new JButton("Create an Account");
        styleButton(createAccountButton, 15);
        createAccountButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 8, 25, 8);
        formPanel.add(createAccountButton, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Login Prompt
        JPanel loginPromptPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        loginPromptPanel.setOpaque(false);
        loginPromptLabel = new JLabel("Already have an account?");
        styleLabel(loginPromptLabel);
        loginButton = new JButton("Login");
        styleButton(loginButton, 13);
        loginPromptPanel.add(loginPromptLabel);
        loginPromptPanel.add(loginButton);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginPromptPanel, gbc);

        centerWrapper.add(formPanel, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        // Action Listeners
        createAccountButton.addActionListener(e -> performRegistration());
        confirmPasswordField.addActionListener(e -> performRegistration());

        backButton.addActionListener(e -> {
            clearInputFields();
            setErrorMessage(" ");
            mainWindow.showMainApplicationView();
        });

        loginButton.addActionListener(e -> {
            clearInputFields();
            setErrorMessage(" ");
            mainWindow.switchToLoginPanel();
        });
    }


    private void performRegistration() {
        System.out.println("Registration attempt started");
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        char[] confirmPasswordChars = confirmPasswordField.getPassword();
        String confirmPassword = new String(confirmPasswordChars);
        boolean termsAccepted = termsCheckBox.isSelected();

        Arrays.fill(passwordChars, ' ');
        Arrays.fill(confirmPasswordChars, ' ');

        if (fullName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setErrorMessage("All fields are required.");
            return;
        }

        // Email Format Validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            setErrorMessage("Invalid email format.");
            emailField.requestFocusInWindow();
            return;
        }

        // Password Match Validation
        if (!password.equals(confirmPassword)) {
            setErrorMessage("Passwords do not match.");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        // Password Length Validation
        if (password.length() < 8) {
            setErrorMessage("Password must be at least 8 characters long.");
            passwordField.requestFocusInWindow();
            return;
        }

        // Terms and Services Validation
        if (!termsAccepted) {
            setErrorMessage("You must agree to the Terms and Services.");
            termsCheckBox.requestFocusInWindow();
            return;
        }

        // If all validation passes
        setErrorMessage(" ");
        System.out.println("Registration validated - Attempting backend registration for username: " + username);

        try {
            User user = new User(null, fullName, username, email, password);
            Long userId = userService.registerUser(user);

            // Registration Successful
            System.out.println("User registered successfully with ID: " + userId);
            JOptionPane.showMessageDialog(this,
                    "Registration Successful!\nYou can now log in.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            clearInputFields();
            mainWindow.switchToLoginPanel();

        } catch (SQLException sqlException) {
            System.err.println("Database error during registration: " + sqlException.getMessage());
            String sqlErrorMsg = sqlException.getMessage().toLowerCase();
            String displayMessage;

            if (sqlErrorMsg.contains("unique constraint") || sqlErrorMsg.contains("duplicate key")) {
                if (sqlErrorMsg.contains("username")) {
                    displayMessage = "This username is already taken. Please choose another.";
                    usernameField.requestFocusInWindow();
                } else if (sqlErrorMsg.contains("email")) {
                    displayMessage = "An account with this email already exists. Please use another or log in.";
                    emailField.requestFocusInWindow();
                } else {
                    displayMessage = "An account with these details might already exist.";
                }
            } else {
                displayMessage = "A database error occurred. Please try again later.";
            }
            JOptionPane.showMessageDialog(this, displayMessage, "Registration Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException iae) {
            System.err.println("Registration validation error (from service): " + iae.getMessage());
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Registration Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception backendException) {
            System.err.println("Backend registration error: " + backendException.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Registration failed due to an unexpected error. Please try again.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Requests that the initial focus be set on the full name field
     * for this panel. Should be called after the panel is visible or
     * added to the main window.
     */
    public void requestInitialFocus() {
        SwingUtilities.invokeLater(() -> {
            if (fullNameField != null) {
                fullNameField.requestFocusInWindow();
            }
        });
    }


    /**
     * Clears all input fields and the error message label.
     */
    private void clearInputFields() {
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        termsCheckBox.setSelected(false);
        errorMsgLbl.setText(" ");
    }

    /**
     * Sets the text of the error message label.
     * @param message The message to display, or " " to clear.
     */
    public void setErrorMessage(String message) {
        errorMsgLbl.setText(message == null ? " " : message);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
    }

    public JPanel getRegistrationPanel() {
        return this;
    }
}