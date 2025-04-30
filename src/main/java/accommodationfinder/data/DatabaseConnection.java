package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                "Cozy Studio Near CPUT Dist. Six", "Simplify your student budget! This cozy, " +
                "NSFAS-accredited studio near CPUT District Six offers all-inclusive living for R7500/month. " +
                "Enjoy included uncapped Wi-Fi, utilities, and secure parking. The modern space features a study desk, " +
                "comfy lounge area, and a balcony for fresh air. Perfect base for focused studying in the City Centre. ",
                Accommodation.AccommodationType.STUDIO, "12 Chapel St", "Cape Town City Centre",
                "8001", -33.927, 18.425, new BigDecimal("7500.00"),
                Accommodation.PriceFrequency.PER_MONTH,
                1, 1, 2, true, true, true,
                "12-month lease",
                LocalDateTime.now().plusDays(10), null, true, sampleUser1);
        acc1.getImageUrls().add("https://properliving.co.za/static/b26f993800130e079b54ff6cbfbeac41/add31/HomePageHeroImage.jpg");
        acc1.getImageUrls().add("https://properliving.co.za/static/9197a1844e04e3ed1d437ac0dd121854/47498/selectorDesktopPlusCommunal.jpg");
        acc1.getImageUrls().add("https://properliving.co.za/static/dba2fea957e55fa3b267d260a8ae7a76/c78d4/selectorDesktopStudioPlus3D.png");
        acc1.setListingDate(LocalDateTime.now().minusDays(4));


        Accommodation acc2 = new Accommodation(
                "Premium Studio in Bellville Park ", "Incredible value! Secure this premium, " +
                "modern studio in Bellville Park for just R3800/month. Price includes essential uncapped Wi-Fi, " +
                "utilities, AND secure parking. Perfectly suited for the academic year lease term. " +
                "Don't miss this all-inclusive deal!",
                Accommodation.AccommodationType.STUDIO, "3 Willie Van Schoor Ave", "Bellville Park",
                "7530", -33.888, 18.630, new BigDecimal("6000.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 1, 2,
                true, true, true, "Academic Year",
                LocalDateTime.now().plusDays(9), null, false, sampleUser1);
        acc2.getImageUrls().add("https://properliving.co.za/static/0bb443567899622fa8bce700f9f463f7/47498/selectorDesktopPenthouse1.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/9550718c413081cfa0417f88b7d6db04/47498/selectorDesktopPenthouse2.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/61bb73d615d28e3a466c861b4ae9b71d/47498/selectorDesktopPenthouse3.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/eff924a8617a4ee2228fbd51f53313d1/47498/selectorDesktopPenthouse4.jpg");
        acc2.getImageUrls().add("https://properliving.co.za/static/d3e499607698c480fa3898eb99884668/47498/selectorDesktopPenthouse5.jpg");
        acc2.setListingDate(LocalDateTime.now().minusDays(3));



        Accommodation acc3 = new Accommodation(
                "Affordable Room To Share", "Budget-friendly room available in a shared student house " +
                "at 106 Adderley Street, City Centre. Ideal location within walking distance to district 6 campus" +
                "Rent of R3800/month includes Wi-Fi, utilities, and parking. Enjoy the flexibility of a monthly lease. " +
                "You'll share the 2 bathrooms and common kitchen/living areas with housemates. Note: Not NSFAS accredited",
                Accommodation.AccommodationType.SHARED_ROOM, "106 Adderley Street", "Cape Town City Centre",
                "7530", -33.888, 18.630, new BigDecimal("3600.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 2, 2,
                true, true, true, "Monthly lease",
                LocalDateTime.now().plusDays(5), null, false, sampleUser1);
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2022/05/IMG_20210729_122034-scaled.jpg");
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2020/02/Bronze-Room-106-Adderley-2.png");
        acc3.getImageUrls().add("https://106adderley.co.za/wp-content/uploads/2020/02/Bronze-Room-106-Adderley-3.png");
        acc3.setListingDate(LocalDateTime.now().minusDays(2));


        Accommodation acc4 = new Accommodation(
                "Renovated Room in Shared Mowbray Apartment", "Secure your spot for the next academic year! " +
                "This recently renovated room is available in a modern 2-bedroom, 2-bathroom shared apartment at 77 Main Rd, " +
                "Mowbray. Ideal for students. You'll share the kitchen and living areas with one other housemate. " +
                "The rent is all-inclusive.",
                Accommodation.AccommodationType.APARTMENT, "77 Main Rd", "Mowbray", "7700",
                -33.947, 18.477, new BigDecimal("4500.00"), Accommodation.PriceFrequency.PER_MONTH,
                2, 2, 2, true, true, true,
                "Academic Year", LocalDateTime.now().plusMonths(6), null, true, sampleUser1);

        acc4.setListingDate(LocalDateTime.now().minusDays(1));
        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36402_additional126202224523PM.jpg");
        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36403_additional126202224523PM.jpg");
        acc4.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/lphs_36407_additional126202224524PM.jpg");




        Accommodation acc5 = new Accommodation(
                "Affordable Dorm Room on Kloof Rd (Gardens)", // Added area to title
                "Budget-friendly single dorm room available at 69 Kloof Rd, " +
                        "located in the vibrant Gardens area, close to Cape Town City Centre amenities. " +
                        "Ideal for students seeking focused accommodation. " +
                        "Rent is R3200 per month, including water, electricity, and Wi-Fi. NSFAS accredited. " +
                        "Enjoy your private room space while utilising shared communal facilities " +
                        "(kitchens, lounges, shared bathrooms). " +
                        "Please note parking is not available. **Important:** Available for a limited period from "
                        + LocalDateTime.now().plusWeeks(2).format(DateTimeFormatter.ISO_LOCAL_DATE) + " until " +
                        LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        ", suitable for the upcoming term/semester within an Academic Year lease structure.",
                Accommodation.AccommodationType.DORM,
                "69 Kloof Rd",
                "Gardens / City Centre",
                "8005",
                -33.930, 18.409,
                new BigDecimal("3200.00"),
                Accommodation.PriceFrequency.PER_MONTH,
                1, // 1 Bed
                1, // 1 Bath
                1,
                true, // Internet Included
                true, // Utilities Included
                false, // Parking Available
                "Academic Year", // Lease Term structure
                LocalDateTime.now().plusWeeks(2), // Available From
                LocalDateTime.now().plusMonths(6), // Available Until
                true, // NSFAS Accredited
                sampleUser2 // Listed By
        );
        acc5.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/iq-images-jan-2025/The%20Brickworks/1.jpg");
        acc5.getImageUrls().add("https://afs-pbsa-images-prod.s3.eu-west-2.amazonaws.com/iq-images-jan-2025/The%20Brickworks/5.jpg");
        acc5.setListingDate(LocalDateTime.now().minusDays(3));

        Accommodation acc6 = new Accommodation(
                "Spacious 2-Bedroom Bellville Apartment for Students",
                "Ideal for sharing! R3000 per student This spacious 2-bedroom, 1-bathroom " +
                        "apartment is located right on Riebeek Street in the heart of Bellville. " +
                        "Perfect base for students needing easy access to transport and campuses. " +
                        "The unit includes Wi-Fi and utilities.Please note, no parking is available with the unit. " +
                        "**Lease Information:** Offered on an 'Academic Year' structure, " +
                        "but currently available for a specific term from " +
                        LocalDateTime.now().plusWeeks(2).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " until " + LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        ". Price is R50,000 per semester for the entire apartment.",
                Accommodation.AccommodationType.APARTMENT,
                "22 Riebeek St",
                "Cape Town City Centre",
                "8000",
                -33.919, 18.422,
                new BigDecimal("3000.00"),
                Accommodation.PriceFrequency.PER_SEMESTER,
                2,
                1,
                2,
                true,
                true,
                false,
                "Academic Year",
                LocalDateTime.now().plusWeeks(2), // Available From
                LocalDateTime.now().plusMonths(6), // Available Until
                false,
                sampleUser2
        );
        acc6.getImageUrls().add("https://properliving.co.za/static/5e3af07283e55386943e39a31ea9d30d/7e6d5/one.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/89e132dabd48707fb2f77ccda5e80c3c/7e6d5/two.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/9ebb0a9d451e77d9c5638b55dec914a7/7e6d5/three.webp");
        acc6.getImageUrls().add("https://properliving.co.za/static/b6ee8a514b612aef5f3986a1a58e0bc7/7e6d5/four.webp");
        acc6.setListingDate(LocalDateTime.now().minusDays(10));

        Accommodation acc7 = new Accommodation(
                "Spacious 3-Bedroom CBD Apartment on St Georges Mall",
                "Share with friends! " +
                        "This large 3-bedroom, 2-bathrooms apartment is perfectly located on St Georges Mall " +
                        "in the Cape Town CBD. Offers unparalleled access to transport, shops, and campuses. " +
                        "Rent includes essential Wi-Fi and utilities. NSFAS accredited. ",

                Accommodation.AccommodationType.APARTMENT,
                "1 St Georges Mall",
                "Cape Town City Centre",
                "8001",
                -33.921, 18.420,
                new BigDecimal("4050.00"),
                Accommodation.PriceFrequency.PER_SEMESTER,
                3,
                2,
                3,
                true,
                true,
                true,
                "Academic Year",
                LocalDateTime.now().plusWeeks(2), // Available From
                LocalDateTime.now().plusMonths(6), // Available Until
                true,
                sampleUser2 // Listed By
        );
        acc7.getImageUrls().add("https://properliving.co.za/static/1489698255aa16ee6726fbcd85463d0c/47498/selectorDesktopThreeBedroom.jpg");
        acc7.getImageUrls().add("https://properliving.co.za/static/57f1e1c6755ee1039b591a49e6619969/cb254/selectorDesktopThreeBedroom2.jpg");
        acc7.getImageUrls().add("https://properliving.co.za/static/3f5a82188ff1b98afb6bf16c7e308a65/c78d4/selectorDesktopThreeBedroom3D.png");
        acc7.setListingDate(LocalDateTime.now().minusDays(10));

        Accommodation acc8 = new Accommodation(
                "Shared Room in Mowbray House",
                "Share a spacious room and save on costs! Located near Liesbeek Parkway in the convenient " +
                        "Mowbray area, this room is designed for two occupants. " +
                        "The rent is R3200 per person, per month, including Wi-Fi and utilities." +
                        "You'll share the room itself (equipped with 2 beds), " +
                        "plus communal bathrooms, kitchen, and living areas within the house/apartment. " +
                        "No parking available. Not NSFAS accredited. **Important:** Available for a specific period from "
                        + LocalDateTime.now().plusWeeks(2).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " until " + LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        ", under an 'Academic Year' lease structure.",
                Accommodation.AccommodationType.SHARED_ROOM,
                "Liesbeek Avenue area, Off Liesbeek Pkwy",
                "Mowbray",
                "7925",
                -33.942043, 18.4418392,
                new BigDecimal("3200.00"),
                Accommodation.PriceFrequency.PER_MONTH,
                2,
                1,
                2,
                true,
                true,
                false,
                "Academic Year",
                LocalDateTime.now().plusWeeks(2), // Available From
                LocalDateTime.now().plusMonths(6), // Available Until
                false,
                sampleUser2
        );
        acc8.getImageUrls().add("https://static.student.com/storm-frontend-wp/uploads/2016/01/Student.com-Room-Types-Shared-Room.jpg");
        acc8.getImageUrls().add("https://static.student.com/storm-frontend-wp/uploads/2016/01/Student.com-Room-Types-Bathroom-Types.jpg");
        acc8.setListingDate(LocalDateTime.now());

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