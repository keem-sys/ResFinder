package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:h2:./student_accommodation_db"; // File-based DB in project directory

    /**
     * Gets a new connection to the database.
     * This method ONLY returns a connection, it does not perform schema checks or data initialization.
     *
     * @return A new Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    /**
     * Initializes the database schema (creates tables if they don't exist)
     * and populates sample data if the tables are empty.
     * This should be called ONCE during application startup.
     *
     * @throws SQLException if a database access error occurs during initialization.
     */
    public void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            System.out.println("Initializing database schema and data...");
            createUsersTableIfNotExists(connection);
            createAccommodationsTableIfNotExists(connection);
            initializeSampleDataIfEmpty(connection); // Pass the connection
            System.out.println("Database initialization complete.");
        } catch (SQLException e) {
            System.err.println("FATAL: Database initialization failed: " + e.getMessage());
            throw e;
        }
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
            System.out.println("USERS table created or verified.");
        } catch (SQLException e) {
            System.err.println("Error creating USERS table: " + e.getMessage());
            throw e;
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
                latitude DOUBLE PRECISION,
                longitude DOUBLE PRECISION,
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
                FOREIGN KEY (listed_by_user_id) REFERENCES USERS(id) ON DELETE CASCADE
            );
        """;

        try(Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("ACCOMMODATIONS table created or verified.");
        } catch (SQLException e) {
            System.err.println("Error creating ACCOMMODATIONS table: " + e.getMessage());
            throw e;
        }
    }

    // Initialize Sample Data (Keep this private, called by initializeDatabase)
    private void initializeSampleDataIfEmpty(Connection connection) {
        // Check if accommodations table is empty
        String checkSql = "SELECT COUNT(*) FROM ACCOMMODATIONS";
        try (Statement checkStmt = connection.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("ACCOMMODATIONS table is empty. Initializing sample data...");
                // Pass the existing connection to the insert method
                insertSampleData(connection);
            } else {
                System.out.println("ACCOMMODATIONS table already has data. Skipping sample data insertion.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/inserting sample data: " + e.getMessage());
        }
    }

    // Insert Sample Data
    // his method uses the passed-in connection for DAO operations
    private void insertSampleData(Connection connection) throws SQLException {

        UserDao userDao = new UserDao(this);
        AccommodationDao accommodationDao = new AccommodationDao(this, userDao);

        User sampleUser1 = null;
        User sampleUser2 = null;

        // --- Check/Create Sample Users ---
        try {
            sampleUser1 = userDao.getUserByUsername("landlord1");
            if (sampleUser1 == null) {
                System.out.println("Creating sample user 'landlord1'");
                // TODO: implement placeholder.
                String placeholderHash = "$argon2id$v=19$m=65536,t=2,p=1$placeholderSalt$placeholderHash"; // REPLACE
                sampleUser1 = new User(null, "Sample Landlord", "landlord1", "landlord1@test.com", placeholderHash);
                sampleUser1.setId(userDao.createUser(sampleUser1));
            }
        } catch (SQLException e) {
            System.err.println("Error finding/creating user 'landlord1': " + e.getMessage());
            throw e;
        }

        try {
            sampleUser2 = userDao.getUserByUsername("agent2");
            if (sampleUser2 == null) {
                System.out.println("Creating sample user 'agent2'");
                // TODO  proper password hasher
                String placeholderHash = "$argon2id$v=19$m=65536,t=2,p=1$placeholderSalt2$placeholderHash2"; // REPLACE
                sampleUser2 = new User(null, "Sample Agent", "agent2", "agent2@test.com", placeholderHash);
                sampleUser2.setId(userDao.createUser(sampleUser2));
            }
        } catch (SQLException e) {
            System.err.println("Error finding/creating user 'agent2': " + e.getMessage());
            throw e;
        }

        // Ensure users were actually created/found before proceeding
        if (sampleUser1 == null || sampleUser1.getId() == null || sampleUser2 == null || sampleUser2.getId() == null) {
            throw new SQLException("Failed to obtain valid sample user IDs for sample data insertion.");
        }


        // Create Sample Accommodation Objects (using the obtained user IDs)
        Accommodation acc1 = new Accommodation(
                "Cozy Studio Near CPUT Dist. Six", "A small, clean studio perfect for one student.",
                Accommodation.AccommodationType.STUDIO, "12 Chapel St", "Cape Town", "8001",
                -33.927, 18.425, new BigDecimal("5500.00"), Accommodation.PriceFrequency.PER_MONTH,
                1, 1, 1, true, true, true, "12-month lease",
                LocalDateTime.now().plusDays(10), null, true, sampleUser1);
        acc1.getImageUrls().add("https://properliving.co.za/static/b26f993800130e079b54ff6cbfbeac41/add31/HomePageHeroImage.jpg");


        Accommodation acc2 = new Accommodation(
                "Shared House Room - Bellville Campus", "Room available in a shared student house, walking distance.",
                Accommodation.AccommodationType.SHARED_ROOM, "5 Protea Rd", "Bellville", "7530",
                -33.888, 18.630, new BigDecimal("3800.00"), Accommodation.PriceFrequency.PER_MONTH,
                4, 2, 4, false, true, true, "Semester lease",
                LocalDateTime.now().plusDays(5), null, false, sampleUser2); // Pass the User object
        acc2.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2022/06/DSC09316.jpg");

        Accommodation acc3 = new Accommodation(
                "Modern 2-Bed Apt - Mowbray Area", "Recently renovated apartment, close to transport.",
                Accommodation.AccommodationType.APARTMENT, "77 Main Rd", "Mowbray", "7700",
                -33.947, 18.477, new BigDecimal("9500.00"), Accommodation.PriceFrequency.PER_MONTH,
                2, 1, 2, true, false, true, "Annual lease",
                LocalDateTime.now().plusMonths(1), null, true, sampleUser1);

        acc3.getImageUrls().add("https://youthopportunitieshub.com/wp-content/uploads/2025/01/student24-accomodation-2025-01-14T123023.533.png");



        Accommodation acc4 = new Accommodation(
                "Independent Dorm Room", "Single dorm room available",
                Accommodation.AccommodationType.DORM, "Residence Block C", "Cape Town Centre", "7535",
                -33.885, 18.635, new BigDecimal("3200.00"), Accommodation.PriceFrequency.PER_SEMESTER,
                1, 0, 1, true, true, false, "Academic Year",
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusMonths(6),
                true, sampleUser2);
        acc4.getImageUrls().add("https://campuskey.co.za/wp-content/uploads/2024/07/Independent-Silver-Bedroom-1-scaled.jpg");

        // DAO to insert accommodations
        try {
            accommodationDao.createAccommodation(acc1);
            accommodationDao.createAccommodation(acc2);
            accommodationDao.createAccommodation(acc3);
            accommodationDao.createAccommodation(acc4);
            System.out.println("Sample accommodation data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting sample accommodation data: " + e.getMessage());
            throw e;
        }
    }
}