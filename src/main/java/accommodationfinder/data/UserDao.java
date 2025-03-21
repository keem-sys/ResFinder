package accommodationfinder.data;

import accommodationfinder.auth.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDao {
    // Call DatabaseConnection.java
    private final DatabaseConnection dbConnection;


    public UserDao(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Create User DAO
    public Long createUser(User user) throws SQLException {
        String sql = "INSERT INTO USERS (full_name, username, email, password_hash, registration_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Important: Pass Statement.RETURN_GENERATED_KEYS

            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());
            preparedStatement.setString(5, user.getRegistrationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // Format LocalDateTime to String

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user in database failed, no ID was generated."); // Slightly clearer message
            }
        } catch (SQLException e) {
            System.err.println("Error creating user in database: " + e.getMessage());
            throw e; // Re-throw the exception or handle it as needed
        }
    }



    // Method to get User by Username
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE username = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet); // Helper method to create User object from ResultSet
                } else {
                    return null; // User not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            throw e;
        }
    }

    // Method to get User by Email
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet); // Helper method to create User object from ResultSet
                } else {
                    return null; // User not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            throw e;
        }
    }


    // Check Username exists
    public boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS WHERE username = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Returns true if count > 0 (username exists)
                } else {
                    return false; // Should not happen, but handle case where result set is empty
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if username exists: " + e.getMessage());
            throw e;
        }
    }

    // Check Email Exists
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS WHERE email = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Returns true if count > 0 (email exists)
                } else {
                    return false; // Should not happen, but handle case where result set is empty
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if email exists: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to map a result set row to User object
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));

        String registrationDateString = resultSet.getString("registration_date"); // Assuming date is stored as String in database
        if (registrationDateString != null) {
            user.setRegistrationDate(LocalDateTime.parse(registrationDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        return user;
    }



}
