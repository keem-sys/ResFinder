package accommodationfinder.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FaqPanel extends JPanel {

    private final MainWindow mainWindow;
    private static final Color BACKGROUND_COLOR = new Color(253, 251, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public FaqPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
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

        // FAQ Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Frequently Asked Questions (FAQ)");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add your Q&A pairs here
        addQuestionAndAnswer(contentPanel, "1. Is using ResFinder free for students?",
                "Yes, absolutely! Searching for accommodation, viewing listings, and contacting landlords " +
                        "through our platform is completely free for all students.");

        addQuestionAndAnswer(contentPanel, "2. What does \"NSFAS Accredited\" mean?",
                "This means the accommodation has been officially approved by the " +
                        "National Student Financial Aid Scheme (NSFAS) and " +
                        "meets their standards for safety, quality, and rental price. " +
                        "If you receive NSFAS funding for accommodation, you can only use it for accredited places.");


        addQuestionAndAnswer(contentPanel, "3. How do I filter for NSFAS accredited places?",
                "On the main screen, click the 'Filters' button. In the dialog that appears, " +
                        "check the 'NSFAS Accredited' checkbox under the 'Features' section and click 'Apply Filters'.");

        addQuestionAndAnswer(contentPanel, "4. How can I contact a landlord?",
                "Click 'View More' on any listing to go to the detail page. On the right side, " +
                        "you will find a 'Contact Lister' form. Fill it out and click 'Send Message'.");

        addQuestionAndAnswer(contentPanel, "5. I've sent a message to a landlord. What happens next?",
                " The landlord will receive your message with your contact details. " +
                        "They should then reply to you directly via your email or phone. " +
                        "Response times can vary, so we recommend contacting a few different places " +
                        "you are interested in.");

        addQuestionAndAnswer(contentPanel, "6. The landlord hasn't replied to my message. What should I do?",
                "We encourage landlords to be responsive, " +
                        "but sometimes they are very busy. " +
                        "If you haven't heard back within 2-3 business days, " +
                        "we recommend you continue your search and contact other available listings.");

        addQuestionAndAnswer(contentPanel, "7. What should I do if I think a listing is a scam or " +
                "has incorrect information?", "Please help us keep the community safe! " +
                "Go to our \"Contact Us\" page, select the \"Report a Listing\" category, " +
                "and provide us with the address or title of the listing and a description of the issue. " +
                "We will investigate immediately.");

        addQuestionAndAnswer(contentPanel, "8. Is my personal information secure?",
                "Yes, we take security very seriously. All passwords are encrypted using a secure " +
                        "hashing algorithm, and we do not share your personal information with third parties " +
                        "without your consent.");


        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(253, 251, 245));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addQuestionAndAnswer(JPanel panel, String question, String answer) {
        JLabel lblQuestion = new JLabel("<html><div style='width:800px;'><b>" + question + "</b></div></html>");
        lblQuestion.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(lblQuestion);
        panel.add(Box.createVerticalStrut(5));

        JLabel lblAnswer = new JLabel("<html><div style='width:800px;'>" + answer + "</div></html>");
        lblAnswer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(lblAnswer);
        panel.add(Box.createVerticalStrut(25));
    }

    public JPanel getFaqPanel() {
        return this;
    }

    private void styleButton(JButton button, int fontSize) {
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
    }
}

