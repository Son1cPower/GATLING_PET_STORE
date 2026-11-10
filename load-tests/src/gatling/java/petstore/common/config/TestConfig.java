package petstore.common.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public record TestConfig(
        String env,
        String baseUrl,
        String dataDir,
        String workload,
        List<ScenarioLoad> scenarios,
        Pause pause,
        Thresholds thresholds,
        Duration maxDuration,
        Duration sessionLoopDuration) {

    public record ScenarioLoad(String name, LoadModel load) {}

    public record Pause(Duration min, Duration max) {}

    private static final TestConfig INSTANCE = from(ConfigFactory.load("gatling"));

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
        var workload = root.getString("workload");
        var scenarios = scenarioLoads(root, workload, root.getString("scenario"));
        var thresholds = Thresholds.from(
                root,
                root.getString("threshold"),
                workload,
                scenarios.stream().map(ScenarioLoad::name).toList());

        return new TestConfig(
                env,
                root.getString(envPath + ".baseUrl"),
                root.getString(envPath + ".dataDir"),
                workload,
                scenarios,
                new Pause(root.getDuration("pause.min"), root.getDuration("pause.max")),
                thresholds,
                root.getDuration("maxDuration"),
                root.getDuration("sessionLoop.duration"));
    }

    private static List<ScenarioLoad> scenarioLoads(Config root, String workload, String scenarioFilter) {
        var workloadPath = "workloads." + ConfigUtil.joinPath(workload);
        if (!root.hasPath(workloadPath)) {
            throw new ConfigException("Unknown workload '%s'. Available: %s"
                    .formatted(workload, new TreeSet<>(root.getObject("workloads").keySet())));
        }
        var defined = new TreeSet<>(root.getObject(workloadPath).keySet());

        var requested = Arrays.stream(scenarioFilter.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        var selected = requested.isEmpty() ? List.copyOf(defined) : requested;

        var result = new ArrayList<ScenarioLoad>();
        for (var name : selected) {
            if (!defined.contains(name)) {
                throw new ConfigException("Scenario '%s' is not defined in workload '%s'. Defined: %s"
                        .formatted(name, workload, defined));
            }
            var path = workloadPath + "." + ConfigUtil.joinPath(name);
            result.add(new ScenarioLoad(name, LoadModel.from(root.getConfigList(path), path)));
        }
        return List.copyOf(result);
    }
}
