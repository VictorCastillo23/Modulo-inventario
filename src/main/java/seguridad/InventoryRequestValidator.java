package seguridad;

/**
 * Pre-DB validation for inventory-mutating request payloads
 * ({@code guardarCambios}, {@code guardarSalidas}). Pure, static,
 * dependency-free — no servlet API, no DAO, no I/O — so it is reachable
 * from {@code src/test/java} with no container.
 *
 * <p>This is the first of the three layers documented in the design's
 * SEC-04 testable/untestable boundary: pre-DB validation (here, unit
 * tested), the SQL floor predicate in
 * {@code ProductosDAO.retirarCantidad} (manual only, not unit-testable),
 * and the caller-side reaction in {@link WithdrawalOutcome} (unit tested).
 */
public final class InventoryRequestValidator {

    private InventoryRequestValidator() {
    }

    /**
     * Validates the parallel arrays submitted by the {@code guardarCambios}
     * form: product ids, quantities to add, target status flags, and
     * per-row "was this row touched" flags.
     */
    public static ValidationResult validateEdit(String[] ids, String[] cantidades,
            String[] estatus, String[] modificados) {
        if (ids == null || cantidades == null || estatus == null || modificados == null) {
            return ValidationResult.invalid(ValidationError.REQUIRED_PARAMETER_MISSING);
        }

        int length = ids.length;
        if (cantidades.length != length || estatus.length != length || modificados.length != length) {
            return ValidationResult.invalid(ValidationError.ARRAY_LENGTH_MISMATCH);
        }

        for (int i = 0; i < length; i++) {
            ValidationError idError = validateId(ids[i]);
            if (idError != null) {
                return ValidationResult.invalid(idError);
            }

            ValidationError cantidadError = validateNonNegativeQuantity(cantidades[i]);
            if (cantidadError != null) {
                return ValidationResult.invalid(cantidadError);
            }

            if (!isBooleanLiteral(estatus[i]) || !isBooleanLiteral(modificados[i])) {
                return ValidationResult.invalid(ValidationError.MALFORMED_BOOLEAN);
            }
        }

        return ValidationResult.valid();
    }

    /**
     * Validates the parallel arrays submitted by the {@code guardarSalidas}
     * form: product ids and quantities to withdraw.
     */
    public static ValidationResult validateWithdrawal(String[] ids, String[] cantidades) {
        if (ids == null || cantidades == null) {
            return ValidationResult.invalid(ValidationError.REQUIRED_PARAMETER_MISSING);
        }

        if (ids.length != cantidades.length) {
            return ValidationResult.invalid(ValidationError.ARRAY_LENGTH_MISMATCH);
        }

        for (int i = 0; i < ids.length; i++) {
            ValidationError idError = validateId(ids[i]);
            if (idError != null) {
                return ValidationResult.invalid(idError);
            }

            ValidationError cantidadError = validateNonNegativeQuantity(cantidades[i]);
            if (cantidadError != null) {
                return ValidationResult.invalid(cantidadError);
            }
        }

        return ValidationResult.valid();
    }

    private static ValidationError validateId(String rawId) {
        Integer id = parseInt(rawId);
        if (id == null || id <= 0) {
            return ValidationError.MALFORMED_NUMBER;
        }
        return null;
    }

    /**
     * A negative quantity would invert {@code cantidad - ?} into a stock
     * *increase*, bypassing the SQL floor predicate — so it is a distinct
     * failure from an unparseable number, not folded into
     * {@link ValidationError#MALFORMED_NUMBER}.
     */
    private static ValidationError validateNonNegativeQuantity(String rawCantidad) {
        Integer cantidad = parseInt(rawCantidad);
        if (cantidad == null) {
            return ValidationError.MALFORMED_NUMBER;
        }
        if (cantidad < 0) {
            return ValidationError.NEGATIVE_QUANTITY;
        }
        return null;
    }

    private static Integer parseInt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * {@code Boolean.parseBoolean} silently maps any non-"true" garbage to
     * {@code false} — that is silent semantic drift, not validation. This
     * method requires an exact literal match instead.
     */
    private static boolean isBooleanLiteral(String s) {
        return "true".equals(s) || "false".equals(s);
    }
}
