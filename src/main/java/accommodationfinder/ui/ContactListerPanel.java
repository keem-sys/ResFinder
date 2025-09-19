package accommodationfinder.ui;

import accommodationfinder.auth.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;

public class ContactListerPanel extends JPanel {

    private long accommodationId;

    private JTextField contactNameField;
    private JTextField contactEmailField;
    private JTextField contactPhoneField;
    private JButton sendMessageButton;
    private JLabel listerInfoLabel;

    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);

    public ContactListerPanel() {
        this.accommodationId = -1;
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

        listerInfoLabel = new JLabel("Listed by: ...");
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

        gbc.gridy++; gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    /**
     * Populates the panel with new information for a specific listing.
     * @param accommodationId The ID of the accommodation being viewed.
     * @param lister The user who listed the accommodation.
     */
    public void updatePanelInfo(long accommodationId, User lister) {
        this.accommodationId = accommodationId;
        if (lister != null) {
            listerInfoLabel.setText("Listed by: " + lister.getFullName());
        } else {
            listerInfoLabel.setText("Listed by: Unknown User");
        }
    }

    public void reset() {
        this.accommodationId = -1;
        listerInfoLabel.setText("Listed by: ...");
        contactNameField.setText("");
        contactEmailField.setText("");
        contactPhoneField.setText("");
        updateSendButtonState();
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
        if (accommodationId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Cannot send message. Listing information is not loaded.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = contactNameField.getText().trim();
        System.out.println("--- Sending Message ---");
        System.out.println("From Name: " + name);
        System.out.println("Regarding Listing ID: " + accommodationId);

        JOptionPane.showMessageDialog(this, "Message sent to the lister",
                "Message Sent", JOptionPane.INFORMATION_MESSAGE);

        contactNameField.setText("");
        contactEmailField.setText("");
        contactPhoneField.setText("");
    }
}