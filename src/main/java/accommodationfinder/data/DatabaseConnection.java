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
     * It is called ONCE during application startup.
     *
     * @throws SQLException if a database access error occurs during initialization.
     */
    public void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            System.out.println("Initializing database schema and data...");
            createUsersTableIfNotExists(connection);
            createAccommodationsTableIfNotExists(connection);
            initializeSampleDataIfEmpty(connection);
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

    // Initialize Sample Data
    private void initializeSampleDataIfEmpty(Connection connection) {
        // Check if accommodations table is empty
        String checkSql = "SELECT COUNT(*) FROM ACCOMMODATIONS";
        try (Statement checkStmt = connection.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("ACCOMMODATIONS table is empty. Initializing sample data...");
                insertSampleData(connection);
            } else {
                System.out.println("ACCOMMODATIONS table already has data. Skipping sample data insertion.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/inserting sample data: " + e.getMessage());
        }
    }

    // Insert Sample Data
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
                // TODO: implement HashPassword.
                String placeholderHash = "$argon2id$v=19$m=65536,t=2,p=1$placeholderSalt$placeholderHash";
                sampleUser1 = new User(null, "Peter Xolani", "landlord1", "landlord1@gmail.com", placeholderHash);
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
                // TODO: proper password hasher
                String placeholderHash = "$argon2id$v=19$m=65536,t=2,p=1$placeholderSalt2$placeholderHash2";
                sampleUser2 = new User(null, "Makunyane Dean", "agent2", "agent2@gmail.com", placeholderHash);
                sampleUser2.setId(userDao.createUser(sampleUser2));
            }
        } catch (SQLException e) {
            System.err.println("Error finding/creating user 'agent2': " + e.getMessage());
            throw e;
        }

        // Ensure users are actually created before proceeding
        if (sampleUser1 == null || sampleUser1.getId() == null || sampleUser2 == null || sampleUser2.getId() == null) {
            throw new SQLException("Failed to obtain valid sample user IDs for sample data insertion.");
        }


        // Create Sample Accommodation Objects (using the obtained user IDs)
        Accommodation acc1 = new Accommodation(
                "Cozy Studio Near CPUT Dist. Six", "A small, clean studio perfect for one student.",
                Accommodation.AccommodationType.STUDIO, "12 Chapel St", "Cape Town City Centre", "8001",
                -33.927, 18.425, new BigDecimal("7500.00"), Accommodation.PriceFrequency.PER_MONTH,
                1, 1, 2, true, true, true,
                "12-month lease",
                LocalDateTime.now().plusDays(10), null, true, sampleUser1);
        acc1.getImageUrls().add("https://properliving.co.za/static/b26f993800130e079b54ff6cbfbeac41/add31/HomePageHeroImage.jpg");
        acc1.getImageUrls().add("https://properliving.co.za/static/9197a1844e04e3ed1d437ac0dd121854/47498/selectorDesktopPlusCommunal.jpg");
        acc1.getImageUrls().add("https://properliving.co.za/static/dba2fea957e55fa3b267d260a8ae7a76/c78d4/selectorDesktopStudioPlus3D.png");

        Accommodation acc2 = new Accommodation(
                "Premium Studio ", "Studio Availanle",
                Accommodation.AccommodationType.STUDIO, "3 Willie Van Schoor Ave", "Bellville Park",
                "7530", -33.888, 18.630, new BigDecimal("3800.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 1, 2,
                true, true, true, "Academic Year",
                LocalDateTime.now().plusDays(9), null, false, sampleUser1);
        acc2.getImageUrls().add("https://properliving.co.za/static/0bb443567899622fa8bce700f9f463f7/47498/selectorDesktopPenthouse1.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/9550718c413081cfa0417f88b7d6db04/47498/selectorDesktopPenthouse2.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/61bb73d615d28e3a466c861b4ae9b71d/47498/selectorDesktopPenthouse3.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/eff924a8617a4ee2228fbd51f53313d1/47498/selectorDesktopPenthouse4.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/d3e499607698c480fa3898eb99884668/47498/selectorDesktopPenthouse5.jpg");



        Accommodation acc3 = new Accommodation(
                "Shared House Room", "Room available in a shared student house, walking distance.",
                Accommodation.AccommodationType.SHARED_ROOM, "106 Adderley Street", "Cape Town City Centre",
                "7530", -33.888, 18.630, new BigDecimal("3800.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 2, 2,
                true, true, true, "Monthly lease",
                LocalDateTime.now().plusDays(5), null, false, sampleUser1);
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2022/05/IMG_20210729_122034-scaled.jpg");
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2020/02/Bronze-Room-106-Adderley-2.png");
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2020/02/Bronze-Room-106-Adderley-3.png");


        Accommodation acc4 = new Accommodation(
                "Single Room", "Recently renovated room to rent",
                Accommodation.AccommodationType.APARTMENT, "77 Main Rd", "Mowbray", "7700",
                -33.947, 18.477, new BigDecimal("5500.00"), Accommodation.PriceFrequency.PER_MONTH,
                2, 2, 2, true, true, true,
                "Academic Year", LocalDateTime.now().plusMonths(6), null, true, sampleUser1);

        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36402_additional126202224523PM.jpg");
        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36403_additional126202224523PM.jpg");
        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36407_additional126202224524PM.jpg");




        Accommodation acc5 = new Accommodation(
                "Independent Dorm Room", "Single dorm room available",
                Accommodation.AccommodationType.DORM, "69 Kloof Rd", "Cape Town City Centre", "8005",
                -33.885, 18.635, new BigDecimal("3200.00"), Accommodation.PriceFrequency.PER_MONTH,
                1, 1, 2, true, true, false, "Academic Year",
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusMonths(6),
                true, sampleUser2);
        acc5.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/iq-images-jan-2025/The%20Brickworks/1.jpg");
        acc5.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/iq-images-jan-2025/The%20Brickworks/5.jpg");


        Accommodation acc6 = new Accommodation(
                "Two Bed Room Accommodation", "Spacious two bedroom apartments...",
                Accommodation.AccommodationType.APARTMENT, "22 Riebeek St", "Cape Town City Centre",
                "8000", -33.9197614, 18.3459784, new BigDecimal("3200.00"),
                Accommodation.PriceFrequency.PER_SEMESTER, 1, 0, 1,
                true, true, false, "Academic Year",
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusMonths(6),
                true, sampleUser2);
        acc6.getImageUrls().add("https://properliving.co.za/static/5e3af07283e55386943e39a31ea9d30d/7e6d5/one.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/89e132dabd48707fb2f77ccda5e80c3c/7e6d5/two.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/9ebb0a9d451e77d9c5638b55dec914a7/7e6d5/three.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/b6ee8a514b612aef5f3986a1a58e0bc7/7e6d5/four.webp");

        Accommodation acc7 = new Accommodation(
                "Three Bed Room Accommodation", "Spacious three bed dorm room available",
                Accommodation.AccommodationType.APARTMENT, "City Center, 1 St Georges Mall", "Cape Town Centre",
                "8001", -33.9191531, 18.3459784, new BigDecimal("3500.00"),
                Accommodation.PriceFrequency.PER_SEMESTER, 1, 0, 1,
                true, true, false, "Academic Year",
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusMonths(6),
                true, sampleUser2);
        acc7.getImageUrls().add("https://properliving.co.za/static/1489698255aa16ee6726fbcd85463d0c/47498/selectorDesktopThreeBedroom.jpg");
        acc7.getImageUrls().add("https://properliving.co.za/static/57f1e1c6755ee1039b591a49e6619969/cb254/selectorDesktopThreeBedroom2.jpg");
        acc7.getImageUrls().add("https://properliving.co.za/static/3f5a82188ff1b98afb6bf16c7e308a65/c78d4/selectorDesktopThreeBedroom3D.png");


        Accommodation acc8 = new Accommodation(
                "Room to Share Accommodation", "Spacious  bed dorm room available",
                Accommodation.AccommodationType.SHARED_ROOM, "Liesbeek Avenue, Off Liesbeek Pkwy", "Mowbray",
                "7925", -33.942043, 18.4418392, new BigDecimal("3200.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 1, 2,
                true, true, false, "Academic Year",
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusMonths(6),
                false, sampleUser1);
        acc8.getImageUrls().add("https://static.student.com/storm-frontend-wp/uploads/2016/01/Student.com-Room-Types-Shared-Room.jpg");
        acc8.getImageUrls().add("https://static.student.com/storm-frontend-wp/uploads/2016/01/Student.com-Room-Types-Bathroom-Types.jpg");


        // DAO to insert accommodations
        try {
            accommodationDao.createAccommodation(acc1);
            accommodationDao.createAccommodation(acc2);
            accommodationDao.createAccommodation(acc3);
            accommodationDao.createAccommodation(acc4);
            accommodationDao.createAccommodation(acc5);
            accommodationDao.createAccommodation(acc6);
            accommodationDao.createAccommodation(acc7);
            accommodationDao.createAccommodation(acc8);
            System.out.println("Sample accommodation data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting sample accommodation data: " + e.getMessage());
            throw e;
        }
    }
}