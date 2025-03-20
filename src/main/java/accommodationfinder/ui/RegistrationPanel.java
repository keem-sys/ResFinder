package accommodationfinder.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {
    private JLabel fullNameLbl, usernameLbl, emailLbl, passwordLbl, confirmPasswordLbl, errorMsgLbl;
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;

    public RegistrationPanel() {
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
                String password = new String(passwordChars);
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
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
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
                }

                System.out.println("Full Name: " + fullName);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);


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

    // Method to get the RegistrationPanel (for adding to MainWindow later)
    public JPanel getRegistrationPanel() {
        return this;
    }

    // Method to set error message (for displaying validation errors from backend)
    public void setErrorMessage(String message) {
        errorMsgLbl.setText(message);
    }




}