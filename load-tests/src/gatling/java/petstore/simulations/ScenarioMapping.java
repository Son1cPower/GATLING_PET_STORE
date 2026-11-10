package petstore.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import petstore.common.config.ConfigException;
import petstore.scenarios.BrowsePetsScenario;
import petstore.scenarios.PetLifecycleScenario;
import petstore.scenarios.PetUserStoreScenario;
import petstore.scenarios.StoreOrderScenario;
import petstore.scenarios.UserLifecycleScenario;

public class ScenarioMapping {

    private final Map<String, Supplier<ScenarioBuilder>> scenarios = new LinkedHashMap<>();

    public ScenarioMapping() {
        includePetScenarios();
        includeUserScenarios();
        includeStoreScenarios();
        includeMultiScenarios();
    }

    private void includePetScenarios() {
        scenarios.put("browsePets", new BrowsePetsScenario()::browsePetsScenario);
        scenarios.put("petLifecycle", new PetLifecycleScenario()::petLifecycleScenario);
    }

    private void includeUserScenarios() {
        scenarios.put("userLifecycle", new UserLifecycleScenario()::userLifecycleScenario);
    }

    private void includeStoreScenarios() {
        scenarios.put("storeOrder", new StoreOrderScenario()::storeOrderScenario);
    }

    private void includeMultiScenarios() {
        scenarios.put("petUserStore", new PetUserStoreScenario()::petUserStoreScenario);
    }

    public List<ScenarioBuilder> getScenarios(List<String> names) {
        return names.stream().map(this::getScenario).toList();
    }

    public ScenarioBuilder getScenario(String name) {
        var supplier = scenarios.get(name);
        if (supplier == null) {
            throw new ConfigException("Unknown scenario '%s'. Available: %s".formatted(name, scenarios.keySet()));
        }
        return supplier.get();
    }
}
