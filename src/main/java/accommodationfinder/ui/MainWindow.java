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




        RegistrationPanel registrationPanel = new RegistrationPanel(userService);
        setContentPane(registrationPanel.getRegistrationPanel());

        JLabel titleLabel = new JLabel("Welcome to Res Finder!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        setLocationRelativeTo(null);
    }

}
