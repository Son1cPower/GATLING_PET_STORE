package petstore.common.config;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;

import com.typesafe.config.Config;
import io.gatling.javaapi.core.OpenInjectionStep;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public enum LoadProfile {
    SMOKE(c -> List.of(atOnceUsers(c.getInt("users")))),

    LOAD(LoadProfile::rampThenHold),

    STRESS(c -> List.of(incrementUsersPerSec(c.getDouble("increment"))
            .times(c.getInt("steps"))
            .eachLevelLasting(c.getDuration("stepDuration"))
            .separatedByRampsLasting(c.getDuration("rampBetweenSteps"))
            .startingFrom(c.getDouble("startRate")))),

    SPIKE(c -> {
        var half = c.getDuration("duration").dividedBy(2);
        var baseRate = c.getDouble("baseRate");
        return List.of(
                constantUsersPerSec(baseRate).during(half),
                atOnceUsers(c.getInt("spikeUsers")),
                constantUsersPerSec(baseRate).during(half));
    }),

    SOAK(LoadProfile::rampThenHold);

    private final Function<Config, List<OpenInjectionStep>> injection;

    LoadProfile(Function<Config, List<OpenInjectionStep>> injection) {
        this.injection = injection;
    }

    public List<OpenInjectionStep> injection(Config settings) {
        return injection.apply(settings);
    }

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static LoadProfile from(String value) {
        return Arrays.stream(values())
                .filter(p -> p.key().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ConfigException("Unknown profile '%s'. Available: %s"
                        .formatted(value, Arrays.stream(values()).map(LoadProfile::key).toList())));
    }

    private static List<OpenInjectionStep> rampThenHold(Config c) {
        var rate = c.getDouble("rate");
        return List.of(
                rampUsersPerSec(0).to(rate).during(c.getDuration("rampUp")),
                constantUsersPerSec(rate).during(c.getDuration("duration")));
    }
}
