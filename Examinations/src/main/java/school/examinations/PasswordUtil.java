package school.examinations;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiny password helper. Hashes with SHA-256 so we never store plain-text passwords.
 *
 * Note: SHA-256 is fine for a class project. For a real production system you would
 * want BCrypt / Argon2 with a per-user salt.
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available in every JDK
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
