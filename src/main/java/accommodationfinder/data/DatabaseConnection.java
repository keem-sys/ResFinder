package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.listing.Accommodation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseConnection {

    private static final String PRODUCTION_JDBC_URL = "jdbc:h2:./student_accommodation_db"; // File-based DB in project directory
    private final String jdbcUrl;

    /**
     * Default constructor for production use.
     * It points to the file-based H2 database.
     */
    public DatabaseConnection() {
        this(PRODUCTION_JDBC_URL);
    }

    /**
     * Test-friendly constructor. Allows specifying the JDBC URL.
     *
     *
     * @param jdbcUrl The JDBC connection string to use.
     */
    public DatabaseConnection(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }


    /**
     * Gets a new connection to the database.
     * This method ONLY returns a connection, it does not perform schema checks or data initialization.
     *
     * @return A new Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.jdbcUrl);
    }

    /**
     * Initializes the database schema (creates tables if they don't exist)
     * and populates sample data if the tables are empty.
     * It is called ONCE during application startup.
     *
     * @throws SQLException if a database access error occurs during initialization.
     */
    public void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(this.jdbcUrl)) {
            System.out.println("Initializing database schema and data for..." + this.jdbcUrl);
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
                "Single Bed Room in Cape Town",  "This modern NSFAS-accredited studio apartment " +
                "offers secure, fully furnished student accommodation just minutes from CPUT District Six. " +
                "The unit includes a private bedroom with a comfortable single bed, built-in storage, ceiling fan, " +
                "and blackout curtains. " +
                "A dedicated study area features a built-in desk, shelving, and convenient power outlets, " +
                "providing a quiet and organized workspace. " +
                "The private bathroom is equipped with a modern shower, vanity, and toilet. " +
                "The compact kitchenette includes a fridge, microwave, stove top, and ample cupboard space, " +
                "making meal preparation easy. " +
                "Unlimited Wi-Fi and utilities are included in the R7500 monthly rent, ensuring a " +
                "hassle-free living experience. " + "The apartment also offers excellent natural light, " +
                "balcony access, and a safe, convenient, and productive environment for focused student life.",

                Accommodation.AccommodationType.STUDIO, "12 Chapel St", "Cape Town City Centre",
                "8001", -33.927, 18.425, new BigDecimal("7500.00"),
                Accommodation.PriceFrequency.PER_MONTH,
                1, 1, 2, true, true, true,
                "12-month lease",
                LocalDateTime.now().plusDays(10), null, true, sampleUser1);
        acc1.getImageUrls().add("https://i.imgur.com/Iigin4J.jpeg");
        acc1.getImageUrls().add("https://i.imgur.com/SiU026v.jpeg");
        acc1.getImageUrls().add("https://i.imgur.com/PHqNuZk.jpeg");
        acc1.setListingDate(LocalDateTime.now().minusDays(4));


        Accommodation acc2 = new Accommodation(
                "Premium 1 Bed room in Bellville Park ", "Experience incredible value in this modern, "
                + "premium " + "studio in Belville Park! For just R3800/month, enjoy a comfortable student living " +
                "with all-inclusive " + "living amenities. The bright space features a comfortable bed, " +
                "dedicated study area " + "with a desk and shelving. Benefit from uncapped WI-FI, wall-mounted TV."+
                "This fully equipped unit offers a kitchenette, a bathroom and secure parking. The space is ideal " +
                "for focused students seeking an academic year lease. " +
                "Don't miss this fully equipped student-friendly option in a prime location.",
                Accommodation.AccommodationType.STUDIO, "3 Willie Van Schoor Ave", "Bellville Park",
                "7530", -33.888, 18.630, new BigDecimal("6000.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 1, 2,
                true, true, true, "Academic Year",
                LocalDateTime.now().plusDays(9), null, false, sampleUser1);
        acc2.getImageUrls().add("https://i.imgur.com/jqRSh0n.jpeg");
        acc2.getImageUrls().add("https://i.imgur.com/LwOzY0D.jpeg");
        acc2.getImageUrls().add("https://i.imgur.com/WpikjX9.jpeg");
        acc2.getImageUrls().add("https://i.imgur.com/l1WWeGd.jpeg");
        acc2.setListingDate(LocalDateTime.now().minusDays(3));


        Accommodation acc3 = new Accommodation(
                "Affordable 1 Bedroom Room Shared in Cape Town", "Discover a budget-friendly shared " +
                "living in the heart of Cape Towns City Centre, " +
                "at 106 Adderley Street, City Centre. This modern room offers twin beds, a bedside table and privacy " +
                "blinds. the Ideal location means you're within walking distance to the District Six Campus" +
                "Enjoy all this with a rent of just R3800/month covering Wi-Fi, utilities, parking, plus the " +
                "flexibility of a monthly lease. " +
                "You'll share the 2 bathrooms each fitted with two sinks and common kitchen/living areas with " +
                "housemates. Note: Not NSFAS accredited",

                Accommodation.AccommodationType.SHARED_ROOM, "106 Adderley Street", "Cape Town City Centre",
                "7530", -33.888, 18.630, new BigDecimal("3600.00"),
                Accommodation.PriceFrequency.PER_MONTH, 1, 2, 2,
                true, true, true, "Monthly lease",
                LocalDateTime.now().plusDays(5), null, false, sampleUser1);
        acc3.getImageUrls().add("https://i.imgur.com/jY9Kaus.jpeg");
        acc3.getImageUrls().add("https://i.imgur.com/1Tv2bS5.png");
        acc3.getImageUrls().add("https://i.imgur.com/gXZPGqD.png");
        acc3.setListingDate(LocalDateTime.now().minusDays(2));


        Accommodation acc4 = new Accommodation(
                "2 Bedroom Apartment Shared in Mowbray", "Secure your spot for the next " +
                "academic year! " +
                "This recently renovated room is available in a modern 2-bedroom, 2-bathroom shared apartment at " +
                "77 Main Rd, " +
                "Mowbray. Ideal for students. You'll share the kitchen and living areas with one other housemate. " +
                "This space features a comfortable bed with a large study desk and wardrobes creating the ideal " +
                "study environment. "+
                "The rent is all-inclusive offering a hassle-free living experience.",
                Accommodation.AccommodationType.APARTMENT, "77 Main Rd", "Mowbray", "7700",
                -33.947, 18.477, new BigDecimal("4500.00"), Accommodation.PriceFrequency.PER_MONTH,
                2, 2, 2, true, true, true,
                "Academic Year", LocalDateTime.now().plusMonths(6), null, true,
                sampleUser1);

        acc4.setListingDate(LocalDateTime.now().minusDays(1));
        acc4.getImageUrls().add("https://i.imgur.com/ulvUDmQ.jpeg");
        acc4.getImageUrls().add("https://i.imgur.com/K1Zr5Qn.jpeg");
        acc4.getImageUrls().add("https://i.imgur.com/HPFpug3.jpeg");




        Accommodation acc5 = new Accommodation(
                "1 Bedroom Dorm on Kloof Rd (Gardens)", // Added area to title
                "Secure your space in this budget-friendly single dorm room available at 69 Kloof Rd, " +
                        "located in the vibrant Gardens area, just minutes from Cape Town City Centre amenities. " +
                        "This is Ideal for students seeking focused accommodation. " +
                        "The room comes with a comfortable bed, a dedicated study area with ample storage " +
                        "and shelving."+
                        "At just R3200 per month,the rent is all-inclusive, covering water, electricity, and Wi-Fi. " +
                        "NSFAS accredited. " +
                        "While you enjoy your private room space and en-suite bathroom, you'll also have access to " +
                        "shared communal facilities such as kitchens and lounges " +
                        "Please note parking is not available. *Important:* Available for a limited period from "
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
                1,
                1,
                1,
                true,
                true,
                false,
                "Academic Year",
                LocalDateTime.now().plusWeeks(2),
                LocalDateTime.now().plusMonths(6),
                true,
                sampleUser2
        );
        acc5.getImageUrls().add("https://i.imgur.com/jDF3zhT.jpeg");
        acc5.getImageUrls().add("https://i.imgur.com/ffocXxy.jpeg");
        acc5.setListingDate(LocalDateTime.now().minusDays(3));

        Accommodation acc6 = new Accommodation(
                "Spacious 2 Bedroom Bellville Apartment for Students",
                "Ideal for sharing! R3000 per student This spacious 2-bedroom, 1-bathroom " +
                        "apartment is located right on Riebeek Street in the heart of Bellville. " +
                        "Perfect for students needing easy access to transport and campuses. " +
                        "This offer provides spacious, private bedrooms. Each bright room features a comfortable bed, "
                        + "and a dedicated study area."+
                        "Your rent includes Wi-Fi and utilities. Please note, no parking is available with the unit. " +
                        "*Lease Information:* Offered on an 'Academic Year' structure, " +
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
        acc6.getImageUrls().add("https://i.imgur.com/PJov4Rs.jpeg");
        acc6.getImageUrls().add("https://i.imgur.com/N9EnkS4.jpeg");
        acc6.getImageUrls().add("https://i.imgur.com/rmX0pTz.png");
        acc6.setListingDate(LocalDateTime.now().minusDays(10));

        Accommodation acc7 = new Accommodation(
                "3 Bedroom CBD Apartment on St Georges Mall",
                "Share with friends in this spacious 3-bedroom apartment! " +
                        "This large 3-bedroom, 2-bathrooms apartment is perfectly located on St Georges Mall " +
                        "in the Cape Town CBD.The apartment features bright bedrooms some equipped with twin beds. " +
                        "Enjoy unparalleled access to transport, shops, and campuses right on your doorstep. " +
                        "The apartment includes 2 bathrooms and a compact, functional kitchenette." +
                        "Rent includes essential Wi-Fi and utilities. NSFAS accredited.",

                Accommodation.AccommodationType.APARTMENT,
                "1 St Georges Mall",
                "Cape Town City Centre",
                "8001",
                -33.921, 18.420,
                new BigDecimal("4000.00"),
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
        acc7.getImageUrls().add("https://i.imgur.com/7vtO9OA.jpeg");
        acc7.getImageUrls().add("https://i.imgur.com/RwjCNKB.jpeg");
        acc7.getImageUrls().add("https://i.imgur.com/DkAYU7b.jpeg");
        acc7.setListingDate(LocalDateTime.now().minusDays(10));

        Accommodation acc8 = new Accommodation(
                "Shared Room in Mowbray House",
                "Share a spacious, modern room and save on costs! Conveniently located near Liesbeek Parkway "
                        + "in the Mowbray area, this room is designed for two occupants. " +
                        "This features two comfortable single beds with individual reading lights included. " +
                        "The rent is R3200 per person, per month, including Wi-Fi and utilities." +
                        "You'll share the room itself, " +
                        "plus communal bathrooms, kitchen, and living areas within the house/apartment. " +
                        "No parking available. Not NSFAS accredited. *Important:* Available for a specific period from "
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
        acc8.getImageUrls().add("https://i.imgur.com/ZTbRsfP.jpeg");
        acc8.getImageUrls().add("https://i.imgur.com/Xaw1jWG.jpeg");
        acc8.setListingDate(LocalDateTime.now());

        Accommodation acc9 = new Accommodation(
                "Furnished Ensuite Room in Mowbray",
                "Enjoy privacy and comfort in this stylishly furnished ensuite room, ideal for focused study " +
                        "and relaxation. Located at 1 Raapenberg Road, Mowbray, this modern unit offers a peaceful " +
                        "environment just minutes from key campuses and public transport. " +
                        "This fully private room comes with a cozy double bed, a sleek study desk, and an " +
                        "ensuite bathroom for your exclusive use. You'll also have access to a comfortable living " +
                        "area and a compact kitchenette – perfect for independent student living. " +
                        "The rent is R9000 per week, which includes Wi-Fi and water. Electricity is prepaid. " +
                        "*Please note:* This unit is not NSFAS accredited and no parking is available on the " +
                        "premises. " + "Lease period: From " +
                        LocalDateTime.now().plusWeeks(4).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " until " + LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        ", under a Weekly Lease structure."
                ,
                Accommodation.AccommodationType.APARTMENT,
                "1 Raapenberg Rd",
                "Mowbray",
                "7705",
                -33.9467651, 18.4818226,
                new BigDecimal("9000"),
                Accommodation.PriceFrequency.PER_WEEK,
                1,
                1,
                1,
                false,
                true,
                false,
                "Weekly Lease",
                LocalDateTime.now().plusWeeks(4),
                LocalDateTime.now().plusMonths(6),
                false,
                sampleUser1
        );
        acc9.getImageUrls().add("https://i.imgur.com/mZXKd4E.jpeg");
        acc9.getImageUrls().add("https://i.imgur.com/VAAal9B.jpeg");
        acc9.getImageUrls().add("https://i.imgur.com/IGqZQBS.png");

        Accommodation acc10 = new Accommodation(
                "Private Bedroom to Rent in Mowbray",
                "Secure and spacious two-bedroom student unit in Mowbray – " +
                        "ideal for friends or siblings sharing. Located at 12 Hornsey Road, this modern dorm-style " +
                        "setup offers two private bedrooms with a shared bathroom and communal kitchen/living space. " +
                        "Each bedroom is fully furnished with a comfortable bed, study desk, and storage. " +
                        "The space is bright, secure, and designed for academic life and comfortable living. " +
                        "Rent is R5000 per week per room, inclusive of Wi-Fi and water. Electricity is prepaid. " +
                        "NSFAS accredited. Parking available on request. " +
                        "Lease duration: From " +
                        LocalDateTime.now().plusWeeks(4).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " until " + LocalDateTime.now().plusMonths(5).format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " under a Weekly Lease agreement."
                ,
                Accommodation.AccommodationType.DORM,
                "12 Hornsey Rd",
                "Mowbray",
                "7700",
                -33.9493108, 18.4721028,
                new BigDecimal("5000"),
                Accommodation.PriceFrequency.PER_WEEK,
                2,
                1,
                2,
                false,
                true,
                true,
                "Weekly Lease",
                LocalDateTime.now().plusWeeks(4),
                LocalDateTime.now().plusMonths(5),
                false,
                sampleUser2
        );
        acc10.getImageUrls().add("https://i.imgur.com/e7QIhNU.png");
        acc10.getImageUrls().add("https://i.imgur.com/xda0eGe.png");
        acc10.getImageUrls().add("https://i.imgur.com/vNq3aQC.png");
        acc10.getImageUrls().add("https://i.imgur.com/vW6C0q2.png");

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
            accommodationDao.createAccommodation(acc9);
            accommodationDao.createAccommodation(acc10);
            System.out.println("Sample accommodation data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting sample accommodation data: " + e.getMessage());
            throw e;
        }
    }
}