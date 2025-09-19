package accommodationfinder.service;

import accommodationfinder.auth.User;
import accommodationfinder.data.SavedListingDAO;
import accommodationfinder.data.UserDao;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private SavedListingDAO mockSavedListingDAO;

    private UserService userService;
    private Key testSecretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // secret key for tests
        String secretString = "89a84479f3ce1998304cea5342b530f8d0eae1588aef348a7dc8b399996c91d0t";
        testSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

        userService = new UserService(mockUserDao, mockSavedListingDAO, testSecretKey);
    }

    // Registration Test

    @Test
    @DisplayName("registerUser should succeed for a valid user")
    void registerUser_WithValidUser_ShouldSucceed() throws SQLException {
        // Arrange
        User validUser = new User(null, "Test User", "TESTUSER", "test@example.com", "password123");
        when(mockUserDao.isUsernameExists(anyString())).thenReturn(false);
        when(mockUserDao.isEmailExists(anyString())).thenReturn(false);
        when(mockUserDao.createUser(any(User.class))).thenReturn(1L);

        // Act
        Long newUserId = userService.registerUser(validUser);

        // Assert
        assertNotNull(newUserId);
        assertEquals(1L, newUserId);
        verify(mockUserDao, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("registerUser should fail if username already exists")
    void registerUser_WhenUsernameExists_ShouldThrowSQLException() throws SQLException {
        // Arrange
        User user = new User(null, "Test User", "TESTUSER", "test@example.com", "password123");
        when(mockUserDao.isUsernameExists("TESTUSER")).thenReturn(true);

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            userService.registerUser(user);
        });
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("registerUser should fail for invalid email format")
    void registerUser_WithInvalidEmail_ShouldThrowIllegalArgumentException() {
        // Arrange
        User user = new User(null, "Test User", "TESTUSER", "invalid-email", "password123");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });
    }

    // Login Tests

    @Test
    @DisplayName("loginUser should succeed with correct username and password")
    void loginUser_WithCorrectCredentials_ShouldReturnJwtToken() throws Exception {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = hashPasswordForTest(plainPassword);
        User user = new User(1L, "Test User", "TESTUSER", "test@example.com", hashedPassword);
        when(mockUserDao.getUserByUsername("TESTUSER")).thenReturn(user);

        // Act
        String token = userService.loginUser("TESTUSER", plainPassword);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        Claims claims = userService.validateAndExtractClaims(token);
        assertEquals("TESTUSER", claims.getSubject());
    }

    @Test
    @DisplayName("loginUser should succeed with correct email and password")
    void loginUser_WithCorrectEmail_ShouldReturnJwtToken() throws Exception {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = hashPasswordForTest(plainPassword);
        User user = new User(1L, "Test User", "TESTUSER", "test@example.com", hashedPassword);
        when(mockUserDao.getUserByUsername("test@example.com")).thenReturn(null); // Simulate username lookup failing
        when(mockUserDao.getUserByEmail("test@example.com")).thenReturn(user);   // Email lookup should succeed

        // Act
        String token = userService.loginUser("test@example.com", plainPassword);

        // Assert
        assertNotNull(token);
    }


    @Test
    @DisplayName("loginUser should fail with incorrect password")
    void loginUser_WithIncorrectPassword_ShouldThrowException() throws SQLException {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = hashPasswordForTest(plainPassword);
        User user = new User(1L, "Test User", "TESTUSER", "test@example.com", hashedPassword);
        when(mockUserDao.getUserByUsername("TESTUSER")).thenReturn(user);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.loginUser("TESTUSER", "wrongPassword");
        });
        assertEquals("Invalid password.", exception.getMessage());
    }

    @Test
    @DisplayName("loginUser should fail if user does not exist")
    void loginUser_WithNonExistentUser_ShouldThrowException() throws SQLException {
        // Arrange
        when(mockUserDao.getUserByUsername(anyString())).thenReturn(null);
        when(mockUserDao.getUserByEmail(anyString())).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.loginUser("nouser", "password123");
        });
        assertEquals("Invalid username or email.", exception.getMessage());
    }

    // Logout Tests

    @Test
    @DisplayName("getUserFromToken should return user for a valid token")
    void getUserFromToken_WithValidToken_ShouldReturnUser() throws Exception {
        // Arrange: create a valid user and token
        String plainPassword = "password123";
        String hashedPassword = hashPasswordForTest(plainPassword);
        User user = new User(1L, "Test User", "TESTUSER", "test@example.com", hashedPassword);
        when(mockUserDao.getUserByUsername("TESTUSER")).thenReturn(user);
        String token = userService.loginUser("TESTUSER", plainPassword);

        when(mockUserDao.getUserById(1L)).thenReturn(user);

        // Act
        User userFromToken = userService.getUserFromToken(token);

        // Assert
        assertNotNull(userFromToken);
        assertEquals(1L, userFromToken.getId());
        assertEquals("TESTUSER", userFromToken.getUsername());
    }

    @Test
    @DisplayName("getUserFromToken should return null for an invalid token")
    void getUserFromToken_WithInvalidToken_ShouldReturnNull() {
        // Act
        User userFromToken = userService.getUserFromToken("this.is.not.a.valid.token");

        // Assert
        assertNull(userFromToken);
    }

    // Helper Method
    private String hashPasswordForTest(String plainTextPassword) {
        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = plainTextPassword.toCharArray();
        try {
            return argon2.hash(3, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}