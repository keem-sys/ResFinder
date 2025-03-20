package accommodationfinder.ui;


import accommodationfinder.auth.User;
import accommodationfinder.auth.UserService;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
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

        // **Use FormLayout**
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, 150dlu",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 7dlu, p, 7dlu, p"
        );
        PanelBuilder builder = new PanelBuilder(layout, this);
        CellConstraints cc = new CellConstraints();

        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Title Label
        titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        fullNameLbl =  new JLabel("Name:");
        usernameLbl = new JLabel("Username:");
        emailLbl = new JLabel("Email:");
        passwordLbl = new JLabel("Password:");
        confirmPasswordLbl = new JLabel("Confirm Password:");
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

        int row = 1;

        builder.add(titleLabel, cc.xyw(1, row, 3));
        row += 2;

        builder.addLabel(fullNameLbl.getText(), cc.xy(1, row));
        builder.add(fullNameField, cc.xy(3, row));
        row += 2;
        builder.addLabel(usernameLbl.getText(), cc.xy(1, row));
        builder.add(usernameField, cc.xy(3, row));
        row += 2;
        builder.addLabel(emailLbl.getText(), cc.xy(1, row));
        builder.add(emailField, cc.xy(3, row));
        row += 2;
        builder.addLabel(passwordLbl.getText(), cc.xy(1, row));
        builder.add(passwordField, cc.xy(3, row));
        row += 2;
        builder.addLabel(confirmPasswordLbl.getText(), cc.xy(1, row));
        builder.add(confirmPasswordField, cc.xy(3, row));
        row += 2;

        builder.add(errorMsgLbl, cc.xyw(1, row, 3)); // Error message label spans 3 columns
        row += 2;

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        builder.add(buttonPanel, cc.xyw(1, row, 3)); // Buttons span 3 columns, centered

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