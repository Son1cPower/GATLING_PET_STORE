package petstore.common.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public record TestConfig(
        String env,
        String baseUrl,
        String dataDir,
        List<String> scenarios,
        LoadProfile profile,
        Config profileSettings,
        Pause pause,
        Sla sla,
        Duration maxDuration) {

    public record Pause(Duration min, Duration max) {}

    public record Sla(int p95Ms, int p99Ms, double maxFailedPercent) {}

    private static final TestConfig INSTANCE = from(ConfigFactory.load());

    public static TestConfig testConfig() {
        return INSTANCE;
    }

    static TestConfig from(Config root) {
        var env = root.getString("env");
        var envPath = "environments." + env;
        if (!root.hasPath(envPath)) {
            throw new ConfigException("Unknown env '%s'. Available: %s"
                    .formatted(env, root.getObject("environments").keySet()));
        }
        var profile = LoadProfile.from(root.getString("profile"));
        var scenarios = Arrays.stream(root.getString("scenario").split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();

        return new TestConfig(
                env,
                root.getString(envPath + ".baseUrl"),
                root.getString(envPath + ".dataDir"),
                scenarios,
                profile,
                root.getConfig("profiles." + profile.key()),
                new Pause(root.getDuration("pause.min"), root.getDuration("pause.max")),
                new Sla(root.getInt("sla.p95Ms"), root.getInt("sla.p99Ms"), root.getDouble("sla.maxFailedPercent")),
                root.getDuration("maxDuration"));
    }
}
