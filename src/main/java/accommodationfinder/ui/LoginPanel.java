package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private JLabel usernameOrEmailLabel, passwordLabel, errorMessageLabel, titleLabel;
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    private final UserService userService;

    public LoginPanel(UserService userService) {
        this.userService = userService;


        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, 150dlu", // Columns
                "p, 3dlu, p, 3dlu, p, 7dlu, p, 7dlu, p" // Rows
        );
        FormBuilder builder = FormBuilder.create().layout(layout).padding(new EmptyBorder(12, 12, 12, 12));

        // Title Label
        titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        builder.add(titleLabel).xyw(1, 1, 3); // Row 1, spans 3 columns

        usernameOrEmailLabel = new JLabel("Username or Email:");
        builder.add(usernameOrEmailLabel).xy(1, 3); // Row 3
        usernameOrEmailField = new JTextField(20);
        builder.add(usernameOrEmailField).xy(3, 3);

        passwordLabel = new JLabel("Password:");
        builder.add(passwordLabel).xy(1, 5); // Row 5
        passwordField = new JPasswordField(20);
        builder.add(passwordField).xy(3, 5);

        errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);
        builder.add(errorMessageLabel).xyw(1, 7, 3); // Row 7, spans 3 columns

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        builder.add(buttonPanel).xyw(1, 9, 3); // Row 9, spans 3 columns

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Login button clicked");
                String usernameOrEmail = usernameOrEmailField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                System.out.println("Username or Email: " + usernameOrEmail);
                System.out.println("Password: " + password);

                // **Login Logic - Backend Integration - NEXT STEP**
                // 1. Call userService.loginUser(usernameOrEmail, password)
                // 2. Handle successful login (get JWT, store it, switch to main app view)
                // 3. Handle failed login (display error message)

                // Placeholder - Clear password field after attempt
                passwordField.setText("");
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Register button clicked on Login Panel - Switch to Registration Panel");
                // **NEXT: Implement switching to RegistrationPanel in MainWindow**
            }
        });

        add(builder.build()); // Add the built panel to this JPanel
    }

    public JPanel getLoginPanel() {
        return this;
    }

    public void setErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }
}