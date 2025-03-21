package accommodationfinder.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String JDBC_URL =  "jdbc:h2:./student_accommodation_db"; // File-based DB in project directory

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL);
        createUsersTableIfNotExists(connection); // Call table creation method
        return connection;
    }

    private void createUsersTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS USERS (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                full_name VARCHAR(255) NOT NULL,
                username VARCHAR(255) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                registration_date TIMESTAMP NOT NULL
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("USERS table created or verified."); // Confirmation message
        } catch (SQLException e) {
            System.err.println("Error creating USERS table: " + e.getMessage());
            throw e; // Re-throw exception if table creation fails
        }
    }
}