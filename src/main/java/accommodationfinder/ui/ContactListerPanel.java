package accommodationfinder.ui;

import accommodationfinder.auth.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;

public class ContactListerPanel extends JPanel {

    private final long accommodationId;

    private JTextField contactNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JButton sendMessageButton;
    private JLabel listerInfoLabel;

    // Style Constants
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public ContactListerPanel(long accommodationId) {
        this.accommodationId = accommodationId;
        initComponents();
        setupListeners();
        updateSendButtonState();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Contact Lister"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setOpaque(false);
        setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        listerInfoLabel = new JLabel("Listed by: Loading...");
        listerInfoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        gbc.gridwidth = 2;
        add(listerInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        add(new JLabel("Your Name: *"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contactNameField = new JTextField(15);
        add(contactNameField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("Your Email: *"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contactEmailField = new JTextField(15);
        add(contactEmailField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("Your Phone: "), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contactPhoneField = new JTextField(15);
        add(contactPhoneField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(e -> handleSendMessage());
        add(sendMessageButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0; // Vertical glue
        add(Box.createVerticalGlue(), gbc);
    }

    /**
     * Public method to populate lister info after parent fetches data.
     * @param lister The user who listed the accommodation.
     */
    public void setListerInfo(User lister) {
        if (lister != null) {
            listerInfoLabel.setText("Listed by: " + lister.getFullName());
        } else {
            listerInfoLabel.setText("Listed by: Unknown User");
        }
    }

    private void setupListeners() {
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSendButtonState(); }
            public void removeUpdate(DocumentEvent e) { updateSendButtonState(); }
            public void changedUpdate(DocumentEvent e) {}
        };
        contactNameField.getDocument().addDocumentListener(listener);
        contactEmailField.getDocument().addDocumentListener(listener);
    }

    private void updateSendButtonState() {
        boolean enabled = !contactNameField.getText().trim().isEmpty() &&
                contactEmailField.getText().trim().contains("@");
        sendMessageButton.setEnabled(enabled);
    }

    private void handleSendMessage() {
        String name = contactNameField.getText().trim();
        System.out.println("--- Sending Message (Placeholder) ---");
        System.out.println("From Name: " + name);
        System.out.println("Regarding Listing ID: " + accommodationId);

        JOptionPane.showMessageDialog(this, "Message sent to the lister (Placeholder)",
                "Message Sent", JOptionPane.INFORMATION_MESSAGE);

        contactNameField.setText("");
        contactEmailField.setText("");
        contactPhoneField.setText("");
    }
}