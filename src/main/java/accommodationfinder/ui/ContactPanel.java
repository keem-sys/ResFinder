package accommodationfinder.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Calendar;

public class ContactPanel extends JPanel {

    private final MainWindow mainWindow;

    // UI Components
    private JTextField txtFirstName, txtLastName, txtEmail, txtSubject;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JComboBox<String> cmbCategory;
    private JRadioButton radioStudent, radioLandlord;
    private ButtonGroup userRoleGroup;


    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color FIELD_BACKGROUND_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public ContactPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        // Main Panel Setup
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JButton backButton = new JButton("< Back to Main View");
        styleButton(backButton, 15);
        backButton.addActionListener(e -> this.mainWindow.showMainApplicationView());
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Information Panel
        JPanel infoPanel = createInfoPanel();
        mainContentPanel.add(infoPanel);

        // Right Side
        JPanel formPanel = createFormPanel();
        mainContentPanel.add(formPanel);


        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setOpaque(false);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        JLabel lblCopyRight = new JLabel("© " + year + " ResFinder ");
        lblCopyRight.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCopyRight.setForeground(TEXT_COLOR.darker());
        southPanel.add(lblCopyRight);

        add(mainContentPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Contact Us");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Reach Out!");
        lblSubTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSubTitle.setForeground(TEXT_COLOR);
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescription = new JLabel("<html><div style='width:300px; text-align:left;'>" +
                "We’d love to hear from you! Whether you have questions, feedback, or need support, " +
                "feel free to reach out using the form.</div></html>");
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblDescription.setForeground(TEXT_COLOR);
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblSubTitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblDescription);
        panel.add(Box.createVerticalStrut(30));

        // Alternative Contact Info
        JLabel lblAltContact = new JLabel("<html><div style='width:300px; text-align:left;'>" +
                "<b>Alternative Contact:</b><br>" +
                "For direct inquiries, you can also email us at: support@resfinder.app" +
                "</div></html>");
        lblAltContact.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblAltContact.setForeground(TEXT_COLOR);
        lblAltContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAltContact);

        panel.add(Box.createVerticalStrut(15));

        //FAQ
        JLabel lblFaq = new JLabel("<html><div style='width:300px; text-align:left;'>" +
                "Have a common question? Check out our <b>FAQ</b> section first!" +
                "</div></html>");
        lblFaq.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblFaq.setForeground(TEXT_COLOR);
        lblFaq.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblFaq);


        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Send us a Message",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 14), TEXT_COLOR)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Role
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("I am a:"), gbc);

        gbc.gridy = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setOpaque(false);
        radioStudent = new JRadioButton("Student");
        radioStudent.setOpaque(false);
        radioLandlord = new JRadioButton("Landlord / Property Manager");
        radioLandlord.setOpaque(false);
        userRoleGroup = new ButtonGroup();
        userRoleGroup.add(radioStudent);
        userRoleGroup.add(radioLandlord);
        radioStudent.setSelected(true);
        radioPanel.add(radioStudent);
        radioPanel.add(Box.createHorizontalStrut(10));
        radioPanel.add(radioLandlord);
        panel.add(radioPanel, gbc);

        // Category
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Category:"), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        String[] categories = {"General Inquiry", "Technical Support / Report a Bug",
                "Feedback / Suggestion", "Report a Listing", "Landlord / Partnership Inquiry"};
        cmbCategory = new JComboBox<>(categories);
        cmbCategory.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(cmbCategory, gbc);

        // First Name / Last Name
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.weightx = 0.5;
        txtFirstName = new JTextField(15);
        styleTextField(txtFirstName);
        panel.add(txtFirstName, gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(15);
        styleTextField(txtLastName);
        panel.add(txtLastName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtEmail = new JTextField();
        styleTextField(txtEmail);
        panel.add(txtEmail, gbc);

        // Subject
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Subject:"), gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtSubject = new JTextField();
        styleTextField(txtSubject);
        panel.add(txtSubject, gbc);

        // Message
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Message:"), gbc);

        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtMessage = new JTextArea(5, 30);
        styleTextArea(txtMessage);
        JScrollPane messageScrollPane = new JScrollPane(txtMessage);
        panel.add(messageScrollPane, gbc);

        // Send Button
        gbc.gridx = 0; gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weighty = 0;
        btnSend = new JButton("Send");
        styleButton(btnSend, 13);
        btnSend.addActionListener(e -> sendMessage());
        panel.add(btnSend, gbc);

        return panel;
    }

    private void sendMessage() {
        if (txtFirstName.getText().trim().isEmpty() || txtLastName.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() || txtSubject.getText().trim().isEmpty() ||
                txtMessage.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields before sending.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbCategory.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If validation passes
        JOptionPane.showMessageDialog(this, "Thank you! Your message has been sent successfully!",
                "Message Sent", JOptionPane.INFORMATION_MESSAGE);

        // Clear all fields
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtSubject.setText("");
        txtMessage.setText("");
        cmbCategory.setSelectedIndex(0);
        radioStudent.setSelected(true);

        // Reset editable status in case they were pre-filled for a logged-in user
        txtFirstName.setEditable(true);
        txtLastName.setEditable(true);
        txtEmail.setEditable(true);

        txtFirstName.requestFocusInWindow();
    }

    /**
     * Populates the form with user details if a user is logged in.
     * Also makes the fields non-editable to prevent changes.
     * @param fullName The user's full name.
     * @param email    The user's email address.
     */
    public void setUserDetails(String fullName, String email) {
        if (fullName != null) {
            // split for first/last name
            String[] names = fullName.split(" ", 2);
            txtFirstName.setText(names[0]);
            txtFirstName.setEditable(false);
            if (names.length > 1) {
                txtLastName.setText(names[1]);
                txtLastName.setEditable(false);
            }
        }
        if (email != null) {
            txtEmail.setText(email);
            txtEmail.setEditable(false);
        }
    }


    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(FIELD_BACKGROUND_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND_COLOR.darker()),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBackground(FIELD_BACKGROUND_COLOR);
        area.setForeground(TEXT_COLOR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND_COLOR.darker()),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }


    public JPanel getContactPanel() {
        return this;
    }
}