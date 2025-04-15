package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.service.UserService;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {

    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;
    private JLabel errorMsgLbl; // Only need the error message label field
    private final UserService userService;
    private final MainWindow mainWindow;

    private static final Color PANEL_BACKGROUND_COLOR = new Color(230, 230, 230);

    public RegistrationPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        setLayout(new GridBagLayout());
        setBackground(PANEL_BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formPanel = createFormPanel();
        formPanel.setBackground(PANEL_BACKGROUND_COLOR);
        formPanel.setOpaque(true);

        add(formPanel, gbc);

        SwingUtilities.invokeLater(() -> fullNameField.requestFocusInWindow());
    }

    private JPanel createFormPanel() {
        FormLayout layout = new FormLayout(
                "right:max(50dlu;pref), 4dlu, pref:grow",
                "p, 10dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 7dlu, p, 10dlu, p"
        );

        FormBuilder builder = FormBuilder.create().layout(layout)
                .padding(new EmptyBorder(25, 25, 25, 25));

        // Title Label
        JLabel titleLabel = new JLabel("Sign Up!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        builder.add(titleLabel).xyw(1, 1, 3);

        // Form Fields
        builder.addLabel("Full Name:").xy(1, 3);
        fullNameField = new JTextField(20);
        builder.add(fullNameField).xy(3, 3);

        builder.addLabel("Username:").xy(1, 5);
        usernameField = new JTextField(20);
        builder.add(usernameField).xy(3, 5);

        builder.addLabel("Email:").xy(1, 7);
        emailField = new JTextField(20);
        builder.add(emailField).xy(3, 7);

        builder.addLabel("Password:").xy(1, 9);
        passwordField = new JPasswordField(20);
        builder.add(passwordField).xy(3, 9);

        builder.addLabel("Confirm Password:").xy(1, 11);
        confirmPasswordField = new JPasswordField(20);
        builder.add(confirmPasswordField).xy(3, 11);

        // Error Message
        errorMsgLbl = new JLabel(" ", SwingConstants.CENTER);
        errorMsgLbl.setForeground(Color.RED);
        errorMsgLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        builder.add(errorMsgLbl).xyw(1, 13, 3);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
        buttonPanel.setBackground(PANEL_BACKGROUND_COLOR);
        buttonPanel.setOpaque(false);
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.setPreferredSize(new Dimension(100, 30));
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 15));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        builder.add(buttonPanel).xyw(1, 15, 3);

        // Add Action Listeners
        registerButton.addActionListener(e -> performRegistration());
        cancelButton.addActionListener(e -> {
            clearInputFields();
            setErrorMessage(" ");
            mainWindow.showMainApplicationView();
        });

        confirmPasswordField.addActionListener(e -> performRegistration());


        return builder.build();
    }

    private void performRegistration() {
        System.out.println("Registration button clicked or Enter pressed");
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        char[] confirmPasswordChars = confirmPasswordField.getPassword();
        String confirmPassword = new String(confirmPasswordChars);

        // --- Input Validation ---
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setErrorMessage("All fields are required.");
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            setErrorMessage("Invalid email format.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            setErrorMessage("Passwords do not match.");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        if (password.length() < 8) {
            setErrorMessage("Password must be at least 8 characters long.");
            return;
        }

        setErrorMessage(" "); // Clear previous errors if validation passes
        System.out.println("Registration Validated - Attempting backend registration...");

        try {
            User user = new User(null, fullName, username, email, password);
            Long userId = userService.registerUser(user);

            System.out.println("User registered successfully with ID: " + userId);
            JOptionPane.showMessageDialog(this,
                    "Registration Successful!\nPlease log in.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            clearInputFields();
            mainWindow.switchToLoginPanel();

        } catch (SQLException sqlException) {
            System.err.println("Database error during registration: " + sqlException.getMessage());
            sqlException.printStackTrace();
            if (sqlException.getMessage().contains("Username already exists")) {
                setErrorMessage("Username already exists. Please choose another.");
                usernameField.requestFocusInWindow();
            } else if (sqlException.getMessage().contains("Email already exists")) {
                setErrorMessage("Email already exists. Please use another or log in.");
                emailField.requestFocusInWindow();
            } else {
                setErrorMessage("A database error occurred. Please try again later.");
            }
        } catch (IllegalArgumentException iae) { // Catch validation errors from service
            System.err.println("Registration validation error: " + iae.getMessage());
            setErrorMessage(iae.getMessage()); // Display service validation message
        } catch (Exception backendException) {
            System.err.println("Backend registration error: " + backendException.getMessage());
            backendException.printStackTrace();
            setErrorMessage("Registration failed. An unexpected error occurred.");
        } finally {
            java.util.Arrays.fill(passwordChars, ' ');
            java.util.Arrays.fill(confirmPasswordChars, ' ');
            clearInputFields();
        }
    }

    private void clearInputFields() {
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorMsgLbl.setText(" "); // Clear error message
    }

    public JPanel getRegistrationPanel() {
        return this;
    }

    public void setErrorMessage(String message) {
        errorMsgLbl.setText(message);
    }
}