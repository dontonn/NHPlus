package de.hitec.nhplus.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Utility class for password operations
 */
public class PasswordUtil {

    // Password requirements: mindestens 8 Zeichen, 1 Buchstabe, 1 Zahl, 1 Sonderzeichen
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&+])[A-Za-z\\d@$!%*#?&+]{8,}$"
    );

    /**
     * Creates a SHA-256 hash of the password
     * @param password The password to hash
     * @return The SHA-256 hash as string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a password matches its hash
     * @param password The plain text password
     * @param hash The stored hash
     * @return true if password matches hash
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Gets password requirements description
     * @return Description of password requirements
     */
    public static String getPasswordRequirements() {
        return "Mindestens 8 Zeichen, 1 Buchstabe, 1 Zahl, 1 Sonderzeichen (@$!%*#?&+)";
    }

    /**
     * Checks specific password requirements for detailed feedback
     * @param password The password to check
     * @return String with missing requirements or "OK" if valid
     */
    public static String checkPasswordRequirements(String password) {
        if (password == null || password.isEmpty()) {
            return "Passwort darf nicht leer sein";
        }

        StringBuilder issues = new StringBuilder();

        if (password.length() < 8) {
            issues.append("Mindestens 8 Zeichen. ");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            issues.append("Mindestens 1 Buchstabe. ");
        }

        if (!password.matches(".*\\d.*")) {
            issues.append("Mindestens 1 Zahl. ");
        }

        if (!password.matches(".*[@$!%*#?&+].*")) {
            issues.append("Mindestens 1 Sonderzeichen (@$!%*#?&+). ");
        }

        return issues.length() == 0 ? "OK" : "Fehlt: " + issues.toString().trim();
    }
}