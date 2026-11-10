package petstore.common.config;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;

import com.typesafe.config.Config;
import io.gatling.javaapi.core.ClosedInjectionStep;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class LoadModel {

    private static final String DURING = "during";
    private static final List<String> OPEN_STEPS =
            List.of("atOnceUsers", "nothingFor", "constantUsersPerSec", "rampUsersPerSec");
    private static final List<String> CLOSED_STEPS = List.of("constantConcurrentUsers", "rampConcurrentUsers");

    private final List<OpenInjectionStep> openSteps;
    private final List<ClosedInjectionStep> closedSteps;
    private final String description;

    private LoadModel(List<OpenInjectionStep> openSteps, List<ClosedInjectionStep> closedSteps, String description) {
        this.openSteps = openSteps;
        this.closedSteps = closedSteps;
        this.description = description;
    }

    public PopulationBuilder inject(ScenarioBuilder scenario) {
        return closedSteps.isEmpty() ? scenario.injectOpen(openSteps) : scenario.injectClosed(closedSteps);
    }

    public String describe() {
        return description;
    }

    static LoadModel from(List<? extends Config> steps, String path) {
        if (steps.isEmpty()) {
            throw new ConfigException("'%s' must contain at least one injection step".formatted(path));
        }
        var open = new ArrayList<OpenInjectionStep>();
        var closed = new ArrayList<ClosedInjectionStep>();
        var descriptions = new ArrayList<String>();

        for (var step : steps) {
            var keys = new HashSet<>(step.root().keySet());
            keys.remove(DURING);
            if (keys.size() != 1) {
                throw new ConfigException("Each step in '%s' must have exactly one of %s or %s, got %s"
                        .formatted(path, OPEN_STEPS, CLOSED_STEPS, step.root().keySet()));
            }
            var name = keys.iterator().next();
            switch (name) {
                case "atOnceUsers" -> {
                    var users = step.getInt(name);
                    open.add(atOnceUsers(users));
                    descriptions.add("atOnceUsers(%d)".formatted(users));
                }
                case "nothingFor" -> {
                    var duration = step.getDuration(name);
                    open.add(nothingFor(duration));
                    descriptions.add("nothingFor(%s)".formatted(format(duration)));
                }
                case "constantUsersPerSec" -> {
                    var rate = step.getDouble(name);
                    var during = during(step, path, name);
                    open.add(constantUsersPerSec(rate).during(during));
                    descriptions.add("constantUsersPerSec(%s) during %s".formatted(format(rate), format(during)));
                }
                case "rampUsersPerSec" -> {
                    var range = range(step.getDoubleList(name), path, name);
                    var during = during(step, path, name);
                    open.add(rampUsersPerSec(range.get(0)).to(range.get(1)).during(during));
                    descriptions.add("rampUsersPerSec(%s -> %s) during %s"
                            .formatted(format(range.get(0)), format(range.get(1)), format(during)));
                }
                case "constantConcurrentUsers" -> {
                    var users = step.getInt(name);
                    var during = during(step, path, name);
                    closed.add(constantConcurrentUsers(users).during(during));
                    descriptions.add("constantConcurrentUsers(%d) during %s".formatted(users, format(during)));
                }
                case "rampConcurrentUsers" -> {
                    var range = range(step.getIntList(name), path, name);
                    var during = during(step, path, name);
                    closed.add(rampConcurrentUsers(range.get(0)).to(range.get(1)).during(during));
                    descriptions.add("rampConcurrentUsers(%d -> %d) during %s"
                            .formatted(range.get(0), range.get(1), format(during)));
                }
                default -> throw new ConfigException("Unknown step '%s' in '%s'. Open: %s, closed: %s"
                        .formatted(name, path, OPEN_STEPS, CLOSED_STEPS));
            }
        }

        if (!open.isEmpty() && !closed.isEmpty()) {
            throw new ConfigException("'%s' mixes open %s and closed %s steps; use one model per scenario"
                    .formatted(path, OPEN_STEPS, CLOSED_STEPS));
        }
        var model = closed.isEmpty() ? "open" : "closed";
        return new LoadModel(List.copyOf(open), List.copyOf(closed), model + ": " + String.join(", ", descriptions));
    }

    private static Duration during(Config step, String path, String name) {
        if (!step.hasPath(DURING)) {
            throw new ConfigException("Step '%s' in '%s' needs 'during'".formatted(name, path));
        }
        return step.getDuration(DURING);
    }

    private static <T extends Number> List<T> range(List<T> values, String path, String name) {
        if (values.size() != 2) {
            throw new ConfigException("Step '%s' in '%s' needs [from, to], got %s".formatted(name, path, values));
        }
        return values;
    }

    private static String format(double value) {
        return value == Math.floor(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private static String format(Duration duration) {
        return duration.toString().substring(2).toLowerCase();
    }
}
