package petstore.common;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.exitBlockOnFail;

import io.gatling.javaapi.core.ChainBuilder;
import java.time.Duration;

public final class Flow {

    private Flow() {}

    public static ChainBuilder failFast(ChainBuilder body) {
        return exitBlockOnFail().on(body);
    }

    public static ChainBuilder withTeardown(ChainBuilder body, ChainBuilder teardown) {
        return exec(failFast(body)).exec(teardown);
    }

    public static ChainBuilder of(ChainBuilder setup, ChainBuilder test, ChainBuilder teardown) {
        return withTeardown(exec(setup).exec(test), teardown);
    }

    public static ChainBuilder loop(ChainBuilder setup, ChainBuilder iteration, Duration duration, ChainBuilder teardown) {
        return withTeardown(exec(setup).during(duration).on(iteration), teardown);
    }
}
