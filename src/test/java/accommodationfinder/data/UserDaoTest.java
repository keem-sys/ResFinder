package accommodationfinder.data;

import accommodationfinder.auth.User;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the UserDao class.
 * the tests use in-memory H2 database to verify SQL queries.
 */
class UserDaoTest {


    private static Properties testProperties;
    private DatabaseConnection testDbConnection;
    private UserDao userDao;

    // loads the test database properties

    @BeforeAll
    static void loadTestProperties() throws Exception {
        testProperties = new Properties();
        try (InputStream input = UserDaoTest.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            if (input == null) throw new IllegalStateException("Test jdbc.properties not found!");
            testProperties.load(input);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        testDbConnection = new DatabaseConnection(testProperties.getProperty("jdbc.url"));

        try (Connection conn = testDbConnection.getConnection();
             Statement statement = conn.createStatement();
             InputStream schemaInput = UserDaoTest.class.getClassLoader().getResourceAsStream("schema.sql")) {

            if (schemaInput == null) throw new IllegalStateException("schema.sql not found!");

            String schemaSql = new BufferedReader(new InputStreamReader(schemaInput))
                    .lines().collect(Collectors.joining("\n"));
            statement.execute(schemaSql);
        }

        userDao = new UserDao(testDbConnection);
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("createUser should successfully insert a user and return a valid ID")
    void createUser_ShouldInsertUserAndReturnId() throws SQLException {
        // Arrange
        User user = new User(null, "John Doe", "johndoe", "john.doe@example.com", "hashed_password_123");
        user.setRegistrationDate(LocalDateTime.now());

        // Act
        Long newUserId = userDao.createUser(user);

        // Assert
        assertNotNull(newUserId);
        assertTrue(newUserId > 0);

        // Verification Step: Read back from the database to confirm
        User fetchedUser = userDao.getUserById(newUserId);
        assertNotNull(fetchedUser);
        assertEquals("John Doe", fetchedUser.getFullName());
        // Check against the uppercased version if your DAO enforces it
        assertEquals("johndoe".toUpperCase(), fetchedUser.getUsername());
    }

    @Test
    @DisplayName("getUserByUsername should return null for a non-existent user")
    void getUserByUsername_WhenUserNotExists_ShouldReturnNull() throws SQLException {
        // Act
        User foundUser = userDao.getUserByUsername("nonexistent");
        // Assert
        assertNull(foundUser);
    }

    @Test
    @DisplayName("isUsernameExists should return true for an existing user")
    void isUsernameExists_WhenUserExists_ShouldReturnTrue() throws SQLException {
        // Arrange
        User user = new User(null, "Check User", "CHECKUSER", "check@example.com", "password");
        user.setRegistrationDate(LocalDateTime.now());
        userDao.createUser(user);

        // Act & Assert
        assertTrue(userDao.isUsernameExists("CHECKUSER"));
    }

    @Test
    @DisplayName("isEmailExists should return true for an existing email")
    void isEmailExists_WhenEmailExists_ShouldReturnTrue() throws SQLException {
        // Arrange
        User user = new User(null, "Email Check", "emailcheck", "email@example.com", "password");
        user.setRegistrationDate(LocalDateTime.now());
        userDao.createUser(user);

        // Act & Assert
        assertTrue(userDao.isEmailExists("email@example.com"));
    }

    @Test
    @DisplayName("createUser should throw SQLException for duplicate username")
    void createUser_WithDuplicateUsername_ShouldThrowException() throws SQLException {
        // Arrange: Create first user
        User user1 = new User(null, "User One", "duplicate", "user1@example.com", "pass1");
        user1.setRegistrationDate(LocalDateTime.now());
        userDao.createUser(user1);

        // Create  second user with the same username
        User user2 = new User(null, "User Two", "duplicate", "user2@example.com", "pass2");
        user2.setRegistrationDate(LocalDateTime.now());

        // Act & Assert that an exception is thrown
        assertThrows(SQLException.class, () -> {
            userDao.createUser(user2);
        }, "A SQLException should be thrown for a duplicate username UNIQUE constraint violation.");
    }
}