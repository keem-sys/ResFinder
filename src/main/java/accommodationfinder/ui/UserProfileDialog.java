package accommodationfinder.ui;

import accommodationfinder.auth.User;
import accommodationfinder.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class UserProfileDialog extends JDialog {
    private final UserService userService;
    private final User currentUser;


    // UI
    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton saveChangesButton;
    private JButton cancelButton;


    // Style Constants
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color FIELD_BACKGROUND_COLOR = new Color(230, 230, 230);
    private static final Color READ_ONLY_BG_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);


    public UserProfileDialog(Frame owner, UserService userService, User currentUser) {
        super(owner, "My Profile", true);
        this.userService = userService;
        this.currentUser = currentUser;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 25, 20, 25));
        initComponents();
        addListeners();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;

        // Title
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 8, 25, 8);
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridy++;

        // Full Name
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        // Input Fields
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        fullNameField = new JTextField(25);
        styleTextField(fullNameField, true);
        fullNameField.setText(currentUser.getFullName());
        formPanel.add(fullNameField, gbc);

        gbc.gridy++;
        usernameField = new JTextField(25);
        styleTextField(usernameField, false);
        usernameField.setText(currentUser.getUsername());
        formPanel.add(usernameField, gbc);

        gbc.gridy++;
        emailField = new JTextField(25);
        styleTextField(emailField, false);
        emailField.setText(currentUser.getEmail());
        formPanel.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 8, 8, 8);
        newPasswordField = new JPasswordField(25);
        styleTextField(newPasswordField, true);
        formPanel.add(newPasswordField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 8, 8);
        confirmPasswordField = new JPasswordField(25);
        styleTextField(confirmPasswordField, true);
        formPanel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        saveChangesButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        styleButton(cancelButton, FIELD_BACKGROUND_COLOR, TEXT_COLOR, 13);
        buttonPanel.add(saveChangesButton);
        styleButton(saveChangesButton, FIELD_BACKGROUND_COLOR, TEXT_COLOR, 13);

        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        cancelButton.addActionListener(e -> dispose());

        saveChangesButton.addActionListener(e -> handleSaveChanges());
    }

    private void handleSaveChanges() {
        // Get Data from Fields
        String newFullName = fullNameField.getText().trim();
        char[] newPasswordChars = newPasswordField.getPassword();
        char[] confirmPasswordChars = confirmPasswordField.getPassword();
        String newPassword = new String(newPasswordChars);
        String confirmPassword = new String(confirmPasswordChars);

        boolean nameChanged = !newFullName.equals(currentUser.getFullName());
        boolean passwordChanged = !newPassword.isEmpty();

        // Validation
        if (!nameChanged && !passwordChanged) {
            JOptionPane.showMessageDialog(this,
                    "No changes were made.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (nameChanged && newFullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Full Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (passwordChanged) {
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.length() < 8) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 8 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Updates
        try {
            // Update user profile
            if (nameChanged) {
                currentUser.setFullName(newFullName);
                userService.updateUserFullName(currentUser.getId(), newFullName);
            }

            // Change password
            if (passwordChanged) {
                userService.changeUserPassword(currentUser.getId(), newPassword);
            }


            JOptionPane.showMessageDialog(this, "Profile updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "A database error occurred: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // clear password arrays
            Arrays.fill(newPasswordChars, ' ');
            Arrays.fill(confirmPasswordChars, ' ');
        }
    }

    private void styleTextField(JTextField field, boolean editable) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setEditable(editable);
        field.setBackground(editable ? FIELD_BACKGROUND_COLOR : READ_ONLY_BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND_COLOR.darker(), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
    }

}
