package accommodationfinder.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;

public class DatabaseConnection {

    // Path should be changed after Term 3 to jdbc:h2:./student_accommodation_db for a persistent storage
    // Also change it in the IntelliJ H2 console
    private static final String JDBC_URL =  "jdbc:h2:mem:accommodationfinder";

    // Get JDBC connection and trows SQL Exception if unsuccessful
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            System.err.println("Error getting database connection " + e.getMessage());
            throw e;
        }
    }

}
