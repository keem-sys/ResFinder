package accommodationfinder.data;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void getConnection() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try {
            Connection connection = dbConnection.getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}