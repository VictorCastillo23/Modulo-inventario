package seguridad;

/**
 * Result of a pre-DB validation check performed by
 * {@link InventoryRequestValidator}. Carries a structured
 * {@link ValidationError} rather than a free-text message, so nothing
 * internal can leak into a client-facing error response.
 */
public final class ValidationResult {

    private static final ValidationResult VALID = new ValidationResult(true, null);

    private final boolean valid;
    private final ValidationError error;

    private ValidationResult(boolean valid, ValidationError error) {
        this.valid = valid;
        this.error = error;
    }

    public static ValidationResult valid() {
        return VALID;
    }

    public static ValidationResult invalid(ValidationError error) {
        if (error == null) {
            throw new IllegalArgumentException("error must not be null for an invalid result");
        }
        return new ValidationResult(false, error);
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * @return the failure reason, or {@code null} when {@link #isValid()} is {@code true}
     */
    public ValidationError error() {
        return error;
    }
}
