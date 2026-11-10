package petstore.common;

import static io.gatling.javaapi.core.CoreDsl.details;
import static io.gatling.javaapi.core.CoreDsl.forAll;
import static io.gatling.javaapi.core.CoreDsl.global;

import io.gatling.javaapi.core.Assertion;
import java.util.ArrayList;
import java.util.List;
import petstore.common.config.Thresholds;

public final class Assertions {

    private Assertions() {}

    public static List<Assertion> fromThresholds(Thresholds thresholds) {
        var assertions = new ArrayList<Assertion>();

        var global = thresholds.global();
        global.p95Ms().ifPresent(ms -> assertions.add(global().responseTime().percentile(95.0).lt(ms)));
        global.p99Ms().ifPresent(ms -> assertions.add(global().responseTime().percentile(99.0).lt(ms)));
        global.maxFailedPercent().ifPresent(p -> assertions.add(global().failedRequests().percent().lte(p)));

        var each = thresholds.eachRequest();
        each.p95Ms().ifPresent(ms -> assertions.add(forAll().responseTime().percentile(95.0).lt(ms)));
        each.p99Ms().ifPresent(ms -> assertions.add(forAll().responseTime().percentile(99.0).lt(ms)));

        for (var group : thresholds.groups()) {
            group.p95Ms().ifPresent(ms -> assertions.add(details(group.group()).responseTime().percentile(95.0).lt(ms)));
            group.p99Ms().ifPresent(ms -> assertions.add(details(group.group()).responseTime().percentile(99.0).lt(ms)));
        }
        return List.copyOf(assertions);
    }
}
