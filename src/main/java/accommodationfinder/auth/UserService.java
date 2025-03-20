package accommodationfinder.auth;


import accommodationfinder.data.UserDao;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Long registerUser(User user) throws SQLException {

        if (user.getFullName() == null || user.getFullName().isEmpty() ||
                user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            throw new IllegalArgumentException("All fields are required for registration.");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getPasswordHash().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        try {
            if (userDao.isUsernameExists(user.getUsername())) {
                throw new SQLException("Username already exists");
            }

        } catch (SQLException sqlException) {
            System.err.println("Database error while checking the username" + sqlException.getMessage());
            throw sqlException;
        }

        try {
            if (userDao.isEmailExists(user.getEmail())) {
                throw new SQLException("Email already exists");
            }
        } catch (SQLException sqlException) {
            System.err.println("Database error while checking the email: " + sqlException.getMessage());
            throw sqlException;
        }

        String hashedPassword = hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        user.setRegistrationDate(LocalDateTime.now());

        try {
            Long userId = userDao.createUser(user);
            return userId;
        } catch (SQLException sqlException) {
            System.err.println("Error creating user in the database " + sqlException.getMessage());
            throw sqlException;
        }

    }

    private String hashPassword(String plainTextPassword) {
        int iterations = 2;
        int memory = 65536;
        int parallelism = 1;

        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = plainTextPassword.toCharArray();

        try {
            // Hash password
            return argon2.hash(iterations, memory, parallelism, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}
