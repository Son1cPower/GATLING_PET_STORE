package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.StoreSteps.placeOrder;
import static petstore.steps.StoreSteps.viewOrder;

import io.gatling.javaapi.core.ScenarioBuilder;

public class StoreOrderScenario {

    public ScenarioBuilder storeOrderScenario() {
        return scenario("Store Order")
                .exec(placeOrder)
                .pause(DEFAULT_PAUSE)
                .exec(viewOrder);
    }
}
