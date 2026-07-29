package seguridad;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    @Test
    void givenPlaintext_whenHashedThenVerifiedWithSamePlaintext_thenVerifyReturnsTrue() {
        String hash = PasswordHasher.hash("admin");

        boolean result = PasswordHasher.verify("admin", hash);

        assertThat(result).isTrue();
    }

    @Test
    void givenHash_whenVerifiedWithWrongPlaintext_thenVerifyReturnsFalse() {
        String hash = PasswordHasher.hash("admin");

        boolean result = PasswordHasher.verify("not-admin", hash);

        assertThat(result).isFalse();
    }

    @Test
    void givenTwoHashesOfSamePlaintext_whenCompared_thenTheyDiffer() {
        String first = PasswordHasher.hash("admin");
        String second = PasswordHasher.hash("admin");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void givenNullPlaintext_whenVerified_thenVerifyReturnsFalse() {
        String hash = PasswordHasher.hash("admin");

        boolean result = PasswordHasher.verify(null, hash);

        assertThat(result).isFalse();
    }

    @Test
    void givenBlankPlaintext_whenVerified_thenVerifyReturnsFalse() {
        String hash = PasswordHasher.hash("admin");

        boolean result = PasswordHasher.verify("   ", hash);

        assertThat(result).isFalse();
    }

    @Test
    void givenNullStoredHash_whenVerified_thenVerifyReturnsFalse() {
        boolean result = PasswordHasher.verify("admin", null);

        assertThat(result).isFalse();
    }

    @Test
    void givenLegacyPlaintextStoredValue_whenVerified_thenVerifyReturnsFalseWithoutThrowing() {
        boolean result = PasswordHasher.verify("admin", "admin");

        assertThat(result).isFalse();
    }
}
