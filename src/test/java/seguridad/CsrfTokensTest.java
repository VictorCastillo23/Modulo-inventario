package seguridad;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CsrfTokensTest {

    @Test
    void givenGenerate_whenCalled_thenReturnsNonBlankToken() {
        String token = CsrfTokens.generate();

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void givenGenerate_whenCalled_thenTokenUsesUrlSafeBase64Charset() {
        String token = CsrfTokens.generate();

        assertThat(token).matches("^[A-Za-z0-9_-]+$");
    }

    @Test
    void given1000Tokens_whenGenerated_thenAllAreDistinct() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(CsrfTokens.generate());
        }

        assertThat(tokens).hasSize(1000);
    }

    @Test
    void givenEqualTokens_whenMatched_thenReturnsTrue() {
        String token = CsrfTokens.generate();

        boolean result = CsrfTokens.matches(token, token);

        assertThat(result).isTrue();
    }

    @Test
    void givenDifferentTokens_whenMatched_thenReturnsFalse() {
        String expected = CsrfTokens.generate();
        String actual = CsrfTokens.generate();

        boolean result = CsrfTokens.matches(expected, actual);

        assertThat(result).isFalse();
    }

    @Test
    void givenNullExpected_whenMatched_thenReturnsFalse() {
        boolean result = CsrfTokens.matches(null, CsrfTokens.generate());

        assertThat(result).isFalse();
    }

    @Test
    void givenNullActual_whenMatched_thenReturnsFalse() {
        boolean result = CsrfTokens.matches(CsrfTokens.generate(), null);

        assertThat(result).isFalse();
    }

    @Test
    void givenBothNull_whenMatched_thenReturnsFalse() {
        boolean result = CsrfTokens.matches(null, null);

        assertThat(result).isFalse();
    }

    @Test
    void givenSameTokenValueButDifferentInstances_whenMatched_thenReturnsTrue() {
        String expected = "abc123_-XYZ";
        String actual = new String(expected.toCharArray());

        boolean result = CsrfTokens.matches(expected, actual);

        assertThat(result).isTrue();
    }
}
