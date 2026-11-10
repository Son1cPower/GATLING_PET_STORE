package petstore.simulations;

import static petstore.common.config.TestConfig.testConfig;

import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstore.common.Assertions;
import petstore.common.Protocols;

public abstract class SimulationExtended extends Simulation {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationExtended.class);

    protected final void run(Function<String, ScenarioBuilder> scenarios) {
        var config = testConfig();
        List<PopulationBuilder> populations = config.scenarios().stream()
                .map(s -> s.load().inject(scenarios.apply(s.name())))
                .toList();

        setUp(populations)
                .protocols(Protocols.petStore(config))
                .assertions(Assertions.fromThresholds(config.thresholds()))
                .maxDuration(config.maxDuration());
    }

    @Override
    public void before() {
        var config = testConfig();
        var scenarios = config.scenarios().stream()
                .map(s -> "  %s: %s".formatted(s.name(), s.load().describe()))
                .collect(Collectors.joining(System.lineSeparator()));
        LOG.info(
                "Starting {} | env={} | baseUrl={} | workload={} | thresholds={}{}{}",
                getClass().getSimpleName(),
                config.env(),
                config.baseUrl(),
                config.workload(),
                config.thresholds().name(),
                System.lineSeparator(),
                scenarios);
    }
}
