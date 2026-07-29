package seguridad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure accumulator over the caller-side reaction to
 * {@code ProductosDAO.retirarCantidad}'s per-row applied/rejected result.
 *
 * <p>No DAO reference, no servlet API — the caller feeds it one boolean per
 * row (the DAO's return value) and this class only tracks which product ids
 * were rejected and how many rows were successfully applied. This is the
 * unit-testable half of the SEC-04 boundary; the SQL predicate itself
 * ({@code AND cantidad >= ?}) is verified manually against a live database,
 * not here.
 */
public final class WithdrawalOutcome {

    private final List<Integer> rejectedIds = new ArrayList<>();
    private int appliedCount = 0;

    /**
     * Records that the withdrawal for {@code idProducto} was applied
     * (the DAO update affected exactly one row).
     */
    public void applied(int idProducto) {
        appliedCount++;
    }

    /**
     * Records that the withdrawal for {@code idProducto} was rejected
     * (insufficient stock or unknown product — the DAO update affected
     * zero rows). Callers MUST NOT write a {@code Historico} row for a
     * rejected withdrawal.
     */
    public void rejected(int idProducto) {
        rejectedIds.add(idProducto);
    }

    public boolean hasRejections() {
        return !rejectedIds.isEmpty();
    }

    /**
     * @return rejected product ids in the order {@link #rejected(int)} was
     *         called, as an unmodifiable view
     */
    public List<Integer> rejectedIds() {
        return Collections.unmodifiableList(rejectedIds);
    }

    public int appliedCount() {
        return appliedCount;
    }
}
