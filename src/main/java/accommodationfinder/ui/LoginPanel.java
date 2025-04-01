package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private JLabel usernameOrEmailLabel, passwordLabel, errorMessageLabel, titleLabel, registerPromptLabel;
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton, cancelButton, registrationButton;
    private JCheckBox rememberMeChkBox;
    private final UserService userService;
    private final MainWindow mainWindow;

    private static final Color PANEL_BACKGROUND_COLOR = new Color(230, 230, 230);

    public LoginPanel(UserService userService, MainWindow mainWindow) {
        this.userService = userService;
        this.mainWindow = mainWindow;

        setLayout(new GridBagLayout());
        setBackground(PANEL_BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        SwingUtilities.invokeLater(() -> usernameOrEmailField.requestFocusInWindow());

        // --- Title ---
        titleLabel = new JLabel("Login!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 5, 25, 5);
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Username/Email Row ---
        usernameOrEmailLabel = new JLabel("Username or Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameOrEmailLabel, gbc);

        usernameOrEmailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameOrEmailField, gbc);

        // --- Password Row ---
        passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // --- Remember Me Checkbox ---
        rememberMeChkBox = new JCheckBox("Remember me");
        rememberMeChkBox.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(rememberMeChkBox, gbc);

        // --- Error Message Label (Initially hidden) ---
        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(errorMessageLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Login Button ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(PANEL_BACKGROUND_COLOR);
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 10, 5);
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // --- Sign Up Prompt ---
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        registerPromptLabel = new JLabel("Are you a new user?");
        registrationButton = new JButton("Sign Up!");
        registrationButton.setPreferredSize(new Dimension(101, 30));
        registrationButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerPanel.add(registerPromptLabel);
        registerPanel.add(registrationButton);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 0, 5);
        formPanel.add(registerPanel, gbc);

        // formPanel added to main LoginPanel
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainGbc.fill = GridBagConstraints.NONE;
        add(formPanel, mainGbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Login button clicked");
                String usernameOrEmail = usernameOrEmailField.getText().trim();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                boolean rememberMe = rememberMeChkBox.isSelected();

                System.out.println("Username or Email: " + usernameOrEmail);
                System.out.println("Remember me: " + rememberMe);

                // Input validation
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    setErrorMessage("Username/Email and Password are required");
                    return;
                } else {
                    setErrorMessage(" ");
                }

                try {
                    String jwtToken = userService.loginUser(usernameOrEmail, password);

                    if (rememberMe) {
                        mainWindow.saveJwtToPreferences(jwtToken);
                        System.out.println("JWT token saved due to 'Remember Me' being checked.");
                    } else {
                        mainWindow.saveJwtToPreferences(null);
                        System.out.println("JWT token cleared as 'Remember Me' is not checked.");
                    }

                    mainWindow.showMainApplicationView();
                    System.out.println("Login Successful!");
                    clearInputs();


                } catch (Exception authenticationException) {
                    System.err.println("Login failed: " + authenticationException.getMessage());
                    setErrorMessage("Login failed: Invalid credentials.");
                } finally {
                    passwordField.setText("");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel button clicked - Switch to MainWindow");
                mainWindow.showMainApplicationView();
            }
        });

        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Register button clicked on Login Panel - Switch to Registration Panel");
                SwingUtilities.invokeLater(mainWindow::switchToRegistrationPanel);
            }
        });
    }

    private void clearInputs() {
        usernameOrEmailField.setText("");
        passwordField.setText("");
        rememberMeChkBox.setSelected(false);
        setErrorMessage(" ");
    }


    public JPanel getLoginPanel() {
        return this;
    }

    public void setErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }
}