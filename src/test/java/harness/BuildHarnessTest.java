package harness;

import config.Conexion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BuildHarnessTest {

    @Test
    void givenTestHarness_whenAssertJEvaluatesAKnownTruth_thenSurefireReportsOneExecutedTest() {
        assertThat(Conexion.class.getPackageName()).isEqualTo("config");
    }
}
