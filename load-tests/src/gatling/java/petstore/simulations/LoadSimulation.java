package petstore.simulations;

import static petstore.common.config.TestConfig.testConfig;

public class LoadSimulation extends SimulationExtended {

    private final ScenarioMapping map = new ScenarioMapping();

    {
        run(map.getScenarios(testConfig().scenarios()));
    }
}
