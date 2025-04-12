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


    // Create Users Table
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

    // Create Accommodations Table
    private void createAccommodationsTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS ACCOMMODATIONS (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                type VARCHAR(50) NOT NULL,
                address VARCHAR(255),
                city VARCHAR(100) NOT NULL,
                postal_code VARCHAR(20),
                latitude DOUBLE,
                longitude DOUBLE,
                price DECIMAL(10, 2) NOT NULL,
                price_frequency VARCHAR(50) NOT NULL,
                bedrooms INT,
                bathrooms INT,
                max_occupancy INT,
                internet_included BOOLEAN DEFAULT FALSE NOT NULL,
                utilities_included BOOLEAN DEFAULT FALSE NOT NULL,
                parking_available BOOLEAN DEFAULT FALSE NOT NULL,
                lease_term VARCHAR(255),
                available_from TIMESTAMP,
                available_until TIMESTAMP NULL,
                image_urls TEXT,
                status VARCHAR(50) NOT NULL,
                listing_date TIMESTAMP NOT NULL,
                last_updated_date TIMESTAMP NOT NULL,
                nsfas_accredited BOOLEAN DEFAULT FALSE NOT NULL,
                listed_by_user_id BIGINT NOT NULL,
                FOREIGN_KEY (listed_by_user_id) REFERENCES USERS(id) ON DELETE CASCADE 
            );
        """;

        try(Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Accommodations Table created or executed");
        } catch (SQLException e) {
            System.err.println("Error creating ACCOMMODATIONS table");
            throw e;
        }
    }
}