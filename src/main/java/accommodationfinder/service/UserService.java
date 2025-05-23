package accommodationfinder.service;

import accommodationfinder.auth.User;
import accommodationfinder.data.UserDao;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserService {
    private final UserDao userDao;
    private final Key jwtSecretKey;
    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        this.jwtSecretKey = loadJwtSecretKeyFromConfig();
    }


    private Key loadJwtSecretKeyFromConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find application.properties file!");
            }
            properties.load(input);
            String secretKeyBase64 = properties.getProperty("jwt.secretKey");
            if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
                throw new IllegalStateException("jwt.secretKey property not found in application.properties!");
            }
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyBase64)); // Decode and return the key
        } catch (IOException e) {
            throw new IllegalStateException("Error loading application.properties file!", e);
        }
    }

    public Long registerUser(User user) throws SQLException {

        if (user.getFullName() == null || user.getFullName().isEmpty() ||
                user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            throw new IllegalArgumentException("All fields are required for registration.");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
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

    public String loginUser(String usernameOrEmail, String plainTextPassword) throws Exception {
        User user = null;

        // find user by username
        try {
            user = userDao.getUserByUsername(usernameOrEmail);
        } catch (SQLException e) {
            System.out.println("Unable to get user username");
            e.printStackTrace();
        }

        //  If not found by username, try to find by email
        if (user == null) {
            try {
                user = userDao.getUserByEmail(usernameOrEmail);
            } catch (SQLException e) {
                System.out.println("Unable to get user email");
                e.printStackTrace();
            }
        }




        // User not found by either username or email
        if (user == null) {
            throw new Exception("Invalid username or email."); // TODO: Custom AuthenticationException
        }

        // Password Verification (using Argon2-jvm)
        if (!verifyPassword(plainTextPassword, user.getPasswordHash())) {
            throw new Exception("Invalid password."); // TODO: Custom AuthenticationException
        }

        return generateJwtToken(user);
    }


  
    private String generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("userEmail", user.getEmail());

        return Jwts.builder()
                .claims(claims)
                .issuer("ResFinderApp")
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(jwtSecretKey)
                .compact();
    }

    // JWT method validation using JJWT
    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates the JWT token and extracts user claims if valid.
     *
     * @param jwtToken The JWT token string.
     * @return A map of claims (like userId, username) if the token is valid, null otherwise.
     */
    public Claims validateAndExtractClaims(String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) jwtSecretKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) { // Catch potential errors
            System.err.println("JWT validation/parsing failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the User object based on the ID stored within a valid JWT token.
     *
     * @param jwtToken The JWT token string.
     * @return The User object if the token is valid and the user exists, null otherwise.
     */
    public User getUserFromToken(String jwtToken) {
        Claims claims = validateAndExtractClaims(jwtToken);
        if (claims != null) {
            try {
                // Extract user ID from claims
                Object userIdObj = claims.get("userId");
                Long userId = null;
                if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                }

                if (userId != null) {
                    // Fetch user from DAO using the ID
                    return userDao.getUserById(userId);
                } else {
                    System.err.println("User ID not found or invalid type in JWT claims.");
                    return null;
                }
            } catch (SQLException e) {
                System.err.println("Database error fetching user from token ID: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Error processing claims from token: " + e.getMessage());
                return null;
            }
        }
        return null; // Token invalid or claims couldn't be extracted
    }



    //  Password Verification Method (using Argon2-jvm)
    private boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.verify(hashedPassword, plainTextPassword.toCharArray());
        } catch (Exception e) {
            // Password verification failed
            System.err.println("Password verification error: " + e.getMessage());
            return false; // Verification failed
        }
    }


    private String hashPassword (String plainTextPassword){
        int iterations =  3;
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