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
    private JButton loginButton, registrationButton;
    private JCheckBox rememberMeChkBox;
    private final UserService userService;
    private final MainWindow mainWindow;

    public LoginPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;


        FormLayout layout = new FormLayout(
                "right:pref, 4dlu, 150dlu", // Columns
                "p, 3dlu, p, 3dlu, p, 7dlu, p, 7dlu, p, 7dlu, p, 7dlu, p" // Rows
        );
        FormBuilder builder = FormBuilder.create().layout(layout).padding(new EmptyBorder(12, 12, 12, 12));

        // Title Label
        titleLabel = new JLabel("Login!", SwingConstants.CENTER);
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

        rememberMeChkBox = new JCheckBox("Remember me");
        builder.add(rememberMeChkBox).xyw(3, 7, 1);

        errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);
        builder.add(errorMessageLabel).xyw(1, 9, 3);

        loginButton = new JButton("Login");
        builder.add(loginButton).xyw(1, 11, 3);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel registerLabel = new JLabel("Are you a new user? ");
        registrationButton = new JButton("Sign Up!");
        registerPanel.add(registerLabel);
        registerPanel.add(registrationButton);
        builder.add(registerPanel).xyw(1, 13, 3);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Login button clicked");
                String usernameOrEmail = usernameOrEmailField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                boolean rememberMe = rememberMeChkBox.isSelected();

                System.out.println("Username or Email: " + usernameOrEmail);
                System.out.println("Password: " + password);
                System.out.println("Remember me: " + rememberMe);

                // Input validation
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    setErrorMessage("Username/Email and Password are required");
                    return;
                }

                // Login Logic - Backend Integration
                // 3. Handle failed login (display error message)

                try {
                    // loginUser method called
                    String jwtToken = userService.loginUser(usernameOrEmail, password);

                    // TODO: Store JWT
                    System.out.println("Login Successful! JWT Token: " + jwtToken);
                    setErrorMessage("Login Successful!");

                } catch (Exception authenticationException) {
                    System.err.println("Login failed: " + authenticationException.getMessage());
                    authenticationException.printStackTrace();
                    setErrorMessage("Login failed: " + authenticationException.getMessage());                 }
                finally {
                    passwordField.setText("");
                }

                // Placeholder - Clear password field after attempt
                passwordField.setText("");

            }
        });

        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Register button clicked on Login Panel - Switch to Registration Panel");
                // Redirect to LoginPanel
                SwingUtilities.invokeLater(mainWindow::switchToRegistrationPanel);
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