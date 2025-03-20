package accommodationfinder.ui;

import accommodationfinder.auth.UserService;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* RegistrationPanel registrationPanel = new RegistrationPanel();
        JPanel registrationPanelToDisplay = registrationPanel.getRegistrationPanel();
        add(registrationPanelToDisplay, BorderLayout.CENTER);

         */

        JLabel titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        setLocationRelativeTo(null);
    }

}
