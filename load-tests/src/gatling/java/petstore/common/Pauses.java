package petstore.common;

import static petstore.common.config.TestConfig.testConfig;

import io.gatling.javaapi.core.Session;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public final class Pauses {

    private Pauses() {}

    private static final long MIN_MS = testConfig().pause().min().toMillis();
    private static final long MAX_MS = testConfig().pause().max().toMillis();

    public static final Function<Session, Duration> DEFAULT_PAUSE =
            session -> Duration.ofMillis(ThreadLocalRandom.current().nextLong(MIN_MS, MAX_MS + 1));
}
