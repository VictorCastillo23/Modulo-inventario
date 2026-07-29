package seguridad;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithdrawalOutcomeTest {

    @Test
    void givenNoRows_whenNothingRecorded_thenEmptyOutcomeWithNoRejections() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();

        assertThat(outcome.hasRejections()).isFalse();
        assertThat(outcome.rejectedIds()).isEmpty();
        assertThat(outcome.appliedCount()).isZero();
    }

    @Test
    void givenAllRowsApplied_whenRecorded_thenNoRejectionsAndCorrectAppliedCount() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();

        outcome.applied(1);
        outcome.applied(2);
        outcome.applied(3);

        assertThat(outcome.hasRejections()).isFalse();
        assertThat(outcome.rejectedIds()).isEmpty();
        assertThat(outcome.appliedCount()).isEqualTo(3);
    }

    @Test
    void givenAllRowsRejected_whenRecorded_thenHasRejectionsAndZeroApplied() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();

        outcome.rejected(10);
        outcome.rejected(20);

        assertThat(outcome.hasRejections()).isTrue();
        assertThat(outcome.rejectedIds()).containsExactly(10, 20);
        assertThat(outcome.appliedCount()).isZero();
    }

    @Test
    void givenMixOfAppliedAndRejected_whenRecorded_thenBothCountsAreCorrect() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();

        outcome.applied(1);
        outcome.rejected(2);
        outcome.applied(3);
        outcome.rejected(4);
        outcome.applied(5);

        assertThat(outcome.hasRejections()).isTrue();
        assertThat(outcome.appliedCount()).isEqualTo(3);
        assertThat(outcome.rejectedIds()).containsExactly(2, 4);
    }

    @Test
    void givenRejectionsRecordedOutOfNumericOrder_whenQueried_thenRejectedIdsPreservesInsertionOrder() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();

        outcome.rejected(50);
        outcome.rejected(3);
        outcome.rejected(27);

        assertThat(outcome.rejectedIds()).containsExactly(50, 3, 27);
    }

    @Test
    void givenRejectedIds_whenCallerAttemptsMutation_thenThrowsUnsupportedOperationException() {
        WithdrawalOutcome outcome = new WithdrawalOutcome();
        outcome.rejected(1);

        List<Integer> rejectedIds = outcome.rejectedIds();

        assertThatThrownBy(() -> rejectedIds.add(99))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
