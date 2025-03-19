package accommodationfinder.data;

import accommodationfinder.auth.User;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserDao {
    // Call DatabaseConnection.java
    private final DatabaseConnection dbConnection;


    public UserDao(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Create User DAO
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, username, email, password_hash, registration_date) " +
                "VALUES (?, ?, ?, ?, ?)";


        try (Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setString(5, user.getRegistrationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // Format LocalDateTime to String

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user in database: " + e.getMessage());
            throw e; // Re-throw the exception or handle it as needed
        }

    }


}
