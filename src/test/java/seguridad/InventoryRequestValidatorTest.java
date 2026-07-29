package seguridad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRequestValidatorTest {

    // ---- validateWithdrawal ----

    @Test
    void givenNullIds_whenValidatingWithdrawal_thenRequiredParameterMissing() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(null, new String[]{"1"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.REQUIRED_PARAMETER_MISSING);
    }

    @Test
    void givenNullCantidades_whenValidatingWithdrawal_thenRequiredParameterMissing() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(new String[]{"1"}, null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.REQUIRED_PARAMETER_MISSING);
    }

    @Test
    void givenMismatchedArrayLengths_whenValidatingWithdrawal_thenArrayLengthMismatch() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"1", "2"}, new String[]{"5"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.ARRAY_LENGTH_MISMATCH);
    }

    @Test
    void givenZeroLengthArrays_whenValidatingWithdrawal_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(new String[0], new String[0]);

        assertThat(result.isValid()).isTrue();
        assertThat(result.error()).isNull();
    }

    @Test
    void givenMalformedIdNumber_whenValidatingWithdrawal_thenMalformedNumber() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"not-a-number"}, new String[]{"5"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.MALFORMED_NUMBER);
    }

    @Test
    void givenZeroOrNegativeId_whenValidatingWithdrawal_thenMalformedNumber() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"0"}, new String[]{"5"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.MALFORMED_NUMBER);
    }

    @Test
    void givenMalformedCantidadNumber_whenValidatingWithdrawal_thenMalformedNumber() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"1"}, new String[]{"abc"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.MALFORMED_NUMBER);
    }

    @Test
    void givenCantidadZero_whenValidatingWithdrawal_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"1"}, new String[]{"0"});

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void givenNegativeCantidad_whenValidatingWithdrawal_thenNegativeQuantity() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"1"}, new String[]{"-1"});

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(ValidationError.NEGATIVE_QUANTITY);
    }

    @Test
    void givenAllValidRows_whenValidatingWithdrawal_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateWithdrawal(
                new String[]{"1", "2", "3"}, new String[]{"5", "0", "10"});

        assertThat(result.isValid()).isTrue();
    }

    // ---- validateEdit ----

    @ParameterizedTest
    @MethodSource("invalidEditArrays")
    void givenInvalidArrays_whenValidatingEdit_thenExpectedValidationError(
            String[] ids, String[] cantidades, String[] estatus, String[] modificados,
            ValidationError expectedError) {

        ValidationResult result = InventoryRequestValidator.validateEdit(ids, cantidades, estatus, modificados);

        assertThat(result.isValid()).isFalse();
        assertThat(result.error()).isEqualTo(expectedError);
    }

    static Stream<Arguments> invalidEditArrays() {
        return Stream.of(
                Arguments.of((Object) null, new String[]{"1"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.REQUIRED_PARAMETER_MISSING),
                Arguments.of(new String[]{"1"}, (Object) null, new String[]{"true"}, new String[]{"false"},
                        ValidationError.REQUIRED_PARAMETER_MISSING),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, (Object) null, new String[]{"false"},
                        ValidationError.REQUIRED_PARAMETER_MISSING),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, new String[]{"true"}, (Object) null,
                        ValidationError.REQUIRED_PARAMETER_MISSING),
                Arguments.of(new String[]{"1", "2"}, new String[]{"1"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.ARRAY_LENGTH_MISMATCH),
                Arguments.of(new String[]{"1"}, new String[]{"1", "2"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.ARRAY_LENGTH_MISMATCH),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, new String[]{"true", "false"}, new String[]{"false"},
                        ValidationError.ARRAY_LENGTH_MISMATCH),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, new String[]{"true"}, new String[]{"false", "true"},
                        ValidationError.ARRAY_LENGTH_MISMATCH),
                Arguments.of(new String[]{"not-a-number"}, new String[]{"1"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.MALFORMED_NUMBER),
                Arguments.of(new String[]{"0"}, new String[]{"1"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.MALFORMED_NUMBER),
                Arguments.of(new String[]{"1"}, new String[]{"abc"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.MALFORMED_NUMBER),
                Arguments.of(new String[]{"1"}, new String[]{"-1"}, new String[]{"true"}, new String[]{"false"},
                        ValidationError.NEGATIVE_QUANTITY),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, new String[]{"yes"}, new String[]{"false"},
                        ValidationError.MALFORMED_BOOLEAN),
                Arguments.of(new String[]{"1"}, new String[]{"1"}, new String[]{"true"}, new String[]{"nope"},
                        ValidationError.MALFORMED_BOOLEAN)
        );
    }

    @Test
    void givenZeroLengthArrays_whenValidatingEdit_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateEdit(
                new String[0], new String[0], new String[0], new String[0]);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void givenCantidadZero_whenValidatingEdit_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateEdit(
                new String[]{"1"}, new String[]{"0"}, new String[]{"true"}, new String[]{"false"});

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void givenAllValidRows_whenValidatingEdit_thenValid() {
        ValidationResult result = InventoryRequestValidator.validateEdit(
                new String[]{"1", "2"}, new String[]{"5", "0"}, new String[]{"true", "false"}, new String[]{"true", "false"});

        assertThat(result.isValid()).isTrue();
    }
}
