package seguridad;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Synchronizer-token helpers for CSRF protection.
 *
 * <p>Pure, static, dependency-free (no servlet API, no session access) so it
 * can be unit tested in isolation. Callers are responsible for storing the
 * generated token in the session and comparing it against the value
 * submitted with each state-changing request.
 */
public final class CsrfTokens {

    /** Session attribute name the token is stored under. */
    public static final String SESSION_ATTRIBUTE = "csrfToken";

    /** Request parameter name the token is submitted under. */
    public static final String PARAMETER_NAME = "csrfToken";

    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfTokens() {
    }

    /**
     * Generates a new random token: {@value #TOKEN_BYTES} bytes from
     * {@link SecureRandom}, URL-safe Base64 encoded without padding.
     *
     * @return a fresh, non-blank token
     */
    public static String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Compares the session-held token against the value submitted with a
     * request, in constant time with respect to the compared bytes.
     *
     * @param expected the token stored in the session; may be {@code null}
     * @param actual   the token submitted with the request; may be {@code null}
     * @return {@code true} iff both are non-null and represent the same token
     */
    public static boolean matches(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                actual.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
