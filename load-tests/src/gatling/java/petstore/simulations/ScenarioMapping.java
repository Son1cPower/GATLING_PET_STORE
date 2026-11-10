package petstore.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import petstore.common.config.ConfigException;
import petstore.scenarios.BrowsePetsScenario;
import petstore.scenarios.PetLifecycleScenario;
import petstore.scenarios.StoreOrderScenario;
import petstore.scenarios.UserLifecycleScenario;
import petstore.scenarios.UserSessionLoopScenario;
import petstore.scenarios.UserSessionScenario;

public class ScenarioMapping {

    private final Map<String, Supplier<ScenarioBuilder>> scenarios = new LinkedHashMap<>();

    public ScenarioMapping() {
        scenarios.put("browsePets", new BrowsePetsScenario()::browsePetsScenario);
        scenarios.put("petLifecycle", new PetLifecycleScenario()::petLifecycleScenario);
        scenarios.put("userLifecycle", new UserLifecycleScenario()::userLifecycleScenario);
        scenarios.put("userSession", new UserSessionScenario()::userSessionScenario);
        scenarios.put("userSessionLoop", new UserSessionLoopScenario()::userSessionLoopScenario);
        scenarios.put("storeOrder", new StoreOrderScenario()::storeOrderScenario);
    }

    public ScenarioBuilder getScenario(String name) {
        var supplier = scenarios.get(name);
        if (supplier == null) {
            throw new ConfigException("Unknown scenario '%s'. Available: %s".formatted(name, scenarios.keySet()));
        }
        return supplier.get();
    }
}
