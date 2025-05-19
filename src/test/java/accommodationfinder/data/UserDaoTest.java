package accommodationfinder.data;
import accommodationfinder.auth.User;
import accommodationfinder.data.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private UserDao userDao;
    private DatabaseConnection databaseConnection;

    @BeforeEach
    void setUp() throws SQLException {
        databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        userDao = new UserDao((DatabaseConnection) connection);
    }

    @Test
    void createUser() throws SQLException {
        User user = new User(null, "John Doe", "johndoe", "john@example.com", "hashedPassword");
        Long userId = userDao.createUser(user);
        assertNotNull(userId, "User ID should not be null");

        User createdUser = userDao.getUserByUsername("johndoe");
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals("John Doe", createdUser.getFullName());
        assertEquals("john@example.com", createdUser.getEmail());
    }

    @Test
    void getUserByUsername() throws SQLException {
        User user = userDao.getUserByUsername("johndoe");
        assertNotNull(user, "User should not be null");
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void getUserByEmail() throws SQLException {
        User user = userDao.getUserByEmail("john@example.com");
        assertNotNull(user, "User should not be null");
        assertEquals("johndoe", user.getUsername());
    }
}