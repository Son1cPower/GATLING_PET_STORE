package petstore.common;

import static io.gatling.javaapi.core.CoreDsl.global;

import io.gatling.javaapi.core.Assertion;
import java.util.List;
import petstore.common.config.TestConfig;

public final class Assertions {

    private Assertions() {}

    public static List<Assertion> defaultAssertions(TestConfig.Sla sla) {
        return List.of(
                global().responseTime().percentile(95.0).lt(sla.p95Ms()),
                global().responseTime().percentile(99.0).lt(sla.p99Ms()),
                global().failedRequests().percent().lte(sla.maxFailedPercent()));
    }
}
