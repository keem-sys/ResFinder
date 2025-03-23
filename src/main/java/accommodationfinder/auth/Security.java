package accommodationfinder.auth; // Make sure package name is correct

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class Security {

    public static SecretKey generateSecretKey() {
        // Generate a secure SecretKey for JWT signing using HS256 algorithm
        // Keys.secretKeyFor(SignatureAlgorithm.HS256) generates a random key suitable for HS256

        return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    }

    public static void main(String[] args) {
        // Example usage (for testing and demonstration)
        SecretKey secretKey = Security.generateSecretKey();
        System.out.println("Generated Secret Key (Base64 encoded):");
        System.out.println(io.jsonwebtoken.io.Encoders.BASE64.encode(secretKey.getEncoded())); // Base64 encode for display/storage
        System.out.println("\n**IMPORTANT SECURITY WARNING:**");
        System.out.println("**DO NOT store hardcoded secret keys in your code!**");
        System.out.println("**Store the generated secret key securely in a configuration file or environment variable.**");
        System.out.println("**Replace the example insecure hardcoded SECRET_KEY_STRING in UserService.java with your securely generated and stored secret key.**");
    }
}