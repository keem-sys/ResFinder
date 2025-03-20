package accommodationfinder.ui; // Make sure this package name matches your project structure

import accommodationfinder.auth.User;
import accommodationfinder.auth.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {
    private JLabel fullNameLbl, usernameLbl, emailLbl, passwordLbl, confirmPasswordLbl, errorMsgLbl;
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;

    private final UserService userService;

    public RegistrationPanel(UserService userService) {
        this.userService = userService;

        setLayout(new GridLayout(7, 2,10, 5));

        fullNameLbl =  new JLabel("Name: ");
        usernameLbl = new JLabel("Username: ");
        emailLbl = new JLabel("Email: ");
        passwordLbl = new JLabel("Password: ");
        confirmPasswordLbl = new JLabel("Confirm Password: ");
        errorMsgLbl = new JLabel("");
        errorMsgLbl.setForeground(Color.RED);

        // TextFields and PasswordFields initialisation
        fullNameField = new JTextField(20);
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        add(fullNameLbl);
        add(fullNameField);
        add(usernameLbl);
        add(usernameField);
        add(emailLbl);
        add(emailField);
        add(passwordLbl);
        add(passwordField);
        add(confirmPasswordLbl);
        add(confirmPasswordField);
        add(errorMsgLbl);
        add(new JLabel());
        add(registerButton);
        add(cancelButton);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Registration button clicked");
                String fullName = fullNameField.getText();
                String username = usernameField.getText();
                String email = emailField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars); // Convert char[] to String for now - Security Note Below!
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
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+@[A-Za-z0-9.-]+$"; // Corrected Regex
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

                System.out.println("Registration Validated - Ready for Backend Integration"); // Updated message

                // **Security Note (Client-Side Password Handling):**
                // In real-world applications, be extra cautious about handling passwords as Strings in memory,
                // even temporarily on the client-side. `char[]` is generally preferred for security,
                // as it can be explicitly cleared from memory after use. However, for this student project,
                // using String for simplicity in UI logic might be acceptable as long as you are hashing
                // the password securely on the backend (which is the most crucial part).

                // **NEXT STEP: Integrate with Java Backend (Authentication Service) here!**
                // You will replace the System.out.println(...) above with code to:
                // 1. Create a User object with the collected data
                // 2. Call a method on your Authentication Service (e.g., userService.registerUser(...))
                // 3. Handle the response from the service (success or error)
                // 4. Update UI based on the response (display success message or error messages from backend)

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
                    // Clear password fields after attempting registration (regardless of success or failure)
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    confirmPasswordField.setText(""); // Clear confirm password as well
                }


                // Clear password fields after processing
                passwordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for cancel action
                System.out.println("Cancel button clicked");
                //  switch to LoginPanel or clear the RegistrationPanel
            }
        });
    }
    // Helper method to clear input fields
    private void clearInputFields() {
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    // Method to get the RegistrationPanel (for adding to MainWindow later)
    public JPanel getRegistrationPanel() {
        return this;
    }

    // Method to set error message (for displaying validation errors from backend)
    public void setErrorMessage(String message) {
        errorMsgLbl.setText(message);
    }
}