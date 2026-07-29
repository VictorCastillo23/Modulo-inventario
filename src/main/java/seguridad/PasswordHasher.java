package seguridad;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * BCrypt password hashing and verification.
 *
 * <p>Pure, static, dependency-free (no DB, no servlet API) so it can be unit
 * tested in isolation. {@link #verify(String, String)} never throws — any
 * malformed or legacy plaintext hash is treated as a non-match instead of
 * propagating an exception up into {@code UsuarioDAO}/{@code LoginController}.
 */
public final class PasswordHasher {

    private static final int COST = 10;

    private PasswordHasher() {
    }

    /**
     * Hashes a plaintext password with a fresh random salt.
     *
     * @param plain the plaintext password; must not be {@code null}
     * @return a BCrypt hash string (60 characters) safe to store
     * @throws IllegalArgumentException if {@code plain} is {@code null}
     */
    public static String hash(String plain) {
        if (plain == null) {
            throw new IllegalArgumentException("plain must not be null");
        }
        return BCrypt.withDefaults().hashToString(COST, plain.toCharArray());
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     *
     * <p>Fails closed: {@code null}/blank input or a malformed/non-BCrypt
     * stored value returns {@code false} rather than throwing.
     *
     * @param plain the plaintext password submitted by the user
     * @param storedHash the BCrypt hash on file for that user
     * @return {@code true} iff {@code plain} hashes to {@code storedHash}
     */
    public static boolean verify(String plain, String storedHash) {
        if (plain == null || plain.isBlank() || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(plain.toCharArray(), storedHash).verified;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
