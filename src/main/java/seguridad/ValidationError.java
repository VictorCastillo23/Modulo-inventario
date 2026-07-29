package seguridad;

/**
 * Structured validation failure codes for {@link InventoryRequestValidator}.
 *
 * <p>Deliberately not accompanied by free-text messages — callers translate
 * these codes into a generic {@code 400} response, so no internal detail
 * (parameter names, raw values) ever reaches the client.
 */
public enum ValidationError {
    REQUIRED_PARAMETER_MISSING,
    ARRAY_LENGTH_MISMATCH,
    MALFORMED_NUMBER,
    NEGATIVE_QUANTITY,
    MALFORMED_BOOLEAN
}
