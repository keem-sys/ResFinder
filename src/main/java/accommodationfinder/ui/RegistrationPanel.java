package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.auth.UserService;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {
    private JLabel fullNameLbl, usernameLbl, emailLbl, passwordLbl, confirmPasswordLbl, errorMsgLbl, titleLabel;
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;

    private final UserService userService;

    public RegistrationPanel(UserService userService) {
        this.userService = userService;

        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, 150dlu",
                "p, 7dlu, p, 7dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 7dlu, p, 7dlu, p"
        );

        FormBuilder builder = FormBuilder.create().layout(layout).padding(new EmptyBorder(12, 12, 12, 12));

        // Title Label
        titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        builder.add(titleLabel).xyw(1, 1, 3); // Row 1, spans 3 columns

        fullNameLbl = new JLabel("Name:");
        builder.add(fullNameLbl).xy(1, 3); // Row 3
        fullNameField = new JTextField(20);
        builder.add(fullNameField).xy(3, 3);

        usernameLbl = new JLabel("Username:");
        builder.add(usernameLbl).xy(1, 5); // Row 5
        usernameField = new JTextField(20);
        builder.add(usernameField).xy(3, 5);

        emailLbl = new JLabel("Email:");
        builder.add(emailLbl).xy(1, 7); // Row 7
        emailField = new JTextField(20);
        builder.add(emailField).xy(3, 7);

        passwordLbl = new JLabel("Password:");
        builder.add(passwordLbl).xy(1, 9); // Row 9
        passwordField = new JPasswordField(20);
        builder.add(passwordField).xy(3, 9);

        confirmPasswordLbl = new JLabel("Confirm Password:");
        builder.add(confirmPasswordLbl).xy(1, 11); // Row 11
        confirmPasswordField = new JPasswordField(20);
        builder.add(confirmPasswordField).xy(3, 11);

        errorMsgLbl = new JLabel("");
        errorMsgLbl.setForeground(Color.RED);
        builder.add(errorMsgLbl).xyw(1, 13, 3); // Row 13, spans 3 columns

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        builder.add(buttonPanel).xyw(1, 15, 3); // Row 15, spans 3 columns, centered

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Registration button clicked");
                String fullName = fullNameField.getText();
                String username = usernameField.getText();
                String email = emailField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars); // TODO: chart[] to String for now
                char[] confirmPasswordChars = confirmPasswordField.getPassword();
                String confirmPassword =  new String(confirmPasswordChars);


                System.out.println("Full Name: " + fullName);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);
                System.out.println("Confirm Password: " + confirmPassword);


                // Registration logic
                // Input validation
                if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()
                        || confirmPassword.isEmpty()) {
                    setErrorMessage("All fields are required");
                    return;
                }

                // Email validation
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()) {
                    setErrorMessage("Invalid email format.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    setErrorMessage("Password do not match");
                    return;
                }

                if (password.length() < 8) {
                    setErrorMessage("Password must be at least 8 characters long");
                    return;
                }

                System.out.println("Registration Validated - Ready for Backend Integration");

                try {
                    User user = new User(null, fullName, username, email, password);
                    Long userId = userService.registerUser(user);

                    setErrorMessage("Registration Successful! You can now login!");
                    clearInputFields();

                    System.out.println("User registered successfully with ID: " + userId);


                } catch (SQLException sqlException) {
                    System.err.println("Database error during registration: " + sqlException.getMessage());
                    sqlException.printStackTrace();
                    setErrorMessage("Database error occurred during registration: " + sqlException.getMessage());

                } catch (Exception backendException) { // Catch other potential backend exceptions
                    System.err.println("Backend registration error: " + backendException.getMessage());
                    backendException.printStackTrace();
                    setErrorMessage("Registration failed: " + backendException.getMessage());
                } finally {
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: switch to LoginPanel or clear the RegistrationPanel
                System.out.println("Cancel button clicked");
            }
        });

        add(builder.build()); // Add the built panel to this JPanel
    }

    // Helper method to clear input fields
    private void clearInputFields() {
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    public JPanel getRegistrationPanel() {
        return this;
    }

    public void setErrorMessage(String message) {
        errorMsgLbl.setText(message);
    }
}