package accommodationfinder.ui;

import accommodationfinder.auth.UserService;
import accommodationfinder.data.DatabaseConnection;
import accommodationfinder.data.UserDao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private DatabaseConnection databaseConnection;
    private UserDao userDao;
    private UserService userService;
    private RegistrationPanel registrationPanel;
    private LoginPanel loginPanel;

    public MainWindow() {
        setTitle("Student Accommodation Finder");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            userDao = new UserDao(databaseConnection);
            userService = new UserService(userDao);
        } catch (SQLException e) {
            System.err.println("Error initialising database connection " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initialising database",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        this.registrationPanel = new RegistrationPanel(userService, this);
        this.loginPanel = new LoginPanel(userService, this);
        setContentPane(loginPanel.getLoginPanel());

        JLabel titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        setLocationRelativeTo(null);
    }
    public void switchToRegistrationPanel() {
        setContentPane(registrationPanel.getRegistrationPanel());
        revalidate();
        repaint();
        System.out.println("Switched to registration panel");
    }

    public void switchToLoginPanel() {
        setContentPane(loginPanel.getLoginPanel());
        revalidate();
        repaint();
        System.out.println("Switched to login panel");
    }


}
