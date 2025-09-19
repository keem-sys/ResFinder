package accommodationfinder.data;

import accommodationfinder.auth.User;

import java.sql.*;

public class UserDao {
    private final DatabaseConnection dbConnection;

    public UserDao(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Create User DAO
    public Long createUser(User user) throws SQLException {
        String sql = "INSERT INTO USERS (full_name, username, email, password_hash, registration_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPasswordHash());

            // Convert LocalDateTime to java.sql.TimeStamp
            Timestamp registrationTimestamp = Timestamp.valueOf(user.getRegistrationDate());
            preparedStatement.setTimestamp(5, registrationTimestamp);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user in database failed, no ID was generated.");
            }
        } catch (SQLException e) {
            System.err.printf("Error creating user in database: %s%n", e.getMessage());
            throw e;
        }
    }


    public void updateFullName(Long userId, String newFullName) throws SQLException {
        String sql = "UPDATE users SET full_name = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newFullName);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
        }
    }

    public void updatePassword(Long userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setLong(2, userId);
            pstmt.executeUpdate();
        }
    }

    // Method to get User ById

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, or null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public User getUserById(Long userId) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            throw e;
        }
    }


    // Method to get User by Username
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE LOWER(username) = LOWER(?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
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
        String sql = "SELECT * FROM USERS WHERE LOWER(email) = LOWER(?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
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
                    return false;
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
                    return resultSet.getInt(1) > 0;
                } else {
                    return false;
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

        Timestamp registrationTimestamp = resultSet.getTimestamp("registration_date");
        if (registrationTimestamp != null) {
            user.setRegistrationDate(registrationTimestamp.toLocalDateTime());
        }

        return user;
    }
}