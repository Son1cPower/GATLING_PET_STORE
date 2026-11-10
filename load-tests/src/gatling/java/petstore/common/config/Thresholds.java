package petstore.common.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigUtil;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;

public record Thresholds(String name, Limits global, Limits eachRequest, List<GroupLimits> groups) {

    private static final String DEFAULT = "default";
    private static final String GLOBAL = "global";
    private static final String EACH_REQUEST = "eachRequest";
    private static final Set<String> GLOBAL_KEYS = Set.of("p95Ms", "p99Ms", "maxFailedPercent");
    private static final Set<String> RESPONSE_TIME_KEYS = Set.of("p95Ms", "p99Ms");

    public record Limits(OptionalInt p95Ms, OptionalInt p99Ms, OptionalDouble maxFailedPercent) {

        static final Limits NONE = new Limits(OptionalInt.empty(), OptionalInt.empty(), OptionalDouble.empty());
    }

    public record GroupLimits(String group, OptionalInt p95Ms, OptionalInt p99Ms) {

        GroupLimits stricter(GroupLimits other) {
            return new GroupLimits(group, min(p95Ms, other.p95Ms), min(p99Ms, other.p99Ms));
        }

        private static OptionalInt min(OptionalInt a, OptionalInt b) {
            if (a.isEmpty()) {
                return b;
            }
            if (b.isEmpty()) {
                return a;
            }
            return OptionalInt.of(Math.min(a.getAsInt(), b.getAsInt()));
        }
    }

    static Thresholds from(Config root, String requested, String workload, List<String> scenarios) {
        var available = new TreeSet<>(root.getObject("thresholds").keySet());
        var explicit = !requested.isBlank();
        var name = explicit ? requested.strip() : workload;
        var base = root.getConfig("thresholds." + DEFAULT);

        Config effective;
        String resolved;
        if (root.hasPath("thresholds." + ConfigUtil.joinPath(name))) {
            effective = root.getConfig("thresholds." + ConfigUtil.joinPath(name)).withFallback(base);
            resolved = name.equals(DEFAULT) ? DEFAULT : name + " + " + DEFAULT;
        } else if (explicit) {
            throw new ConfigException("Unknown threshold set '%s'. Available: %s".formatted(name, available));
        } else {
            effective = base;
            resolved = DEFAULT;
        }

        var groups = new LinkedHashMap<String, GroupLimits>();
        for (var scenario : scenarios) {
            var key = ConfigUtil.joinPath(scenario);
            if (!effective.hasPath(key)) {
                continue;
            }
            var scenarioConfig = effective.getConfig(key);
            for (var group : effective.getObject(key).keySet()) {
                var path = "thresholds.%s.%s.\"%s\"".formatted(name, scenario, group);
                var limits = scenarioConfig.getConfig(ConfigUtil.joinPath(group));
                checkKeys(limits, RESPONSE_TIME_KEYS, path);
                groups.merge(group, new GroupLimits(group, optionalInt(limits, "p95Ms"), optionalInt(limits, "p99Ms")),
                        GroupLimits::stricter);
            }
        }

        return new Thresholds(
                resolved,
                limits(effective, GLOBAL, GLOBAL_KEYS, name),
                limits(effective, EACH_REQUEST, RESPONSE_TIME_KEYS, name),
                List.copyOf(groups.values()));
    }

    private static Limits limits(Config config, String key, Set<String> allowed, String name) {
        if (!config.hasPath(key)) {
            return Limits.NONE;
        }
        var limits = config.getConfig(key);
        checkKeys(limits, allowed, "thresholds.%s.%s".formatted(name, key));
        return new Limits(
                optionalInt(limits, "p95Ms"),
                optionalInt(limits, "p99Ms"),
                limits.hasPath("maxFailedPercent")
                        ? OptionalDouble.of(limits.getDouble("maxFailedPercent"))
                        : OptionalDouble.empty());
    }

    private static void checkKeys(Config config, Set<String> allowed, String path) {
        for (var key : config.root().keySet()) {
            if (!allowed.contains(key)) {
                throw new ConfigException("Unknown key '%s' in '%s'. Allowed: %s"
                        .formatted(key, path, new TreeSet<>(allowed)));
            }
        }
    }

    private static OptionalInt optionalInt(Config config, String key) {
        return config.hasPath(key) ? OptionalInt.of(config.getInt(key)) : OptionalInt.empty();
    }
}
