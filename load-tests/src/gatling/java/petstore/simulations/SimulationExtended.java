package petstore.simulations;

import static petstore.common.config.TestConfig.testConfig;

import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petstore.common.Assertions;
import petstore.common.Protocols;

public abstract class SimulationExtended extends Simulation {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationExtended.class);

    protected final void run(List<ScenarioBuilder> scenarios) {
        var config = testConfig();
        var injection = config.profile().injection(config.profileSettings());
        List<PopulationBuilder> populations = scenarios.stream()
                .map(scenario -> scenario.injectOpen(injection))
                .toList();

        setUp(populations)
                .protocols(Protocols.petStore(config))
                .assertions(Assertions.defaultAssertions(config.sla()))
                .maxDuration(config.maxDuration());
    }

    @Override
    public void before() {
        var config = testConfig();
        LOG.info(
                "Starting {} | env={} | baseUrl={} | scenario={} | profile={}",
                getClass().getSimpleName(),
                config.env(),
                config.baseUrl(),
                config.scenarios(),
                config.profile().key());
    }
}
