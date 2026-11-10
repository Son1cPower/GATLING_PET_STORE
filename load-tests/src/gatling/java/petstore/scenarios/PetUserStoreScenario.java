package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.api.pet.PetStatus.AVAILABLE;
import static petstore.api.pet.PetStatus.PENDING;
import static petstore.api.pet.PetStatus.SOLD;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.PetSteps.findPets;
import static petstore.steps.StoreSteps.placeOrder;
import static petstore.steps.StoreSteps.viewOrder;
import static petstore.steps.UserSteps.createUser;
import static petstore.steps.UserSteps.deleteUser;
import static petstore.steps.UserSteps.updateUser;
import static petstore.steps.UserSteps.viewUser;

import io.gatling.javaapi.core.ScenarioBuilder;

public class PetUserStoreScenario {

    public ScenarioBuilder petUserStoreScenario() {
        return scenario("Pet User Store")
                .exec(findPets(AVAILABLE))
                .pause(DEFAULT_PAUSE)
                .exec(findPets(PENDING))
                .pause(DEFAULT_PAUSE)
                .exec(findPets(SOLD))
                .pause(DEFAULT_PAUSE)
                .exec(createUser)
                .pause(DEFAULT_PAUSE)
                .exec(viewUser)
                .pause(DEFAULT_PAUSE)
                .exec(updateUser)
                .pause(DEFAULT_PAUSE)
                .exec(viewUser)
                .pause(DEFAULT_PAUSE)
                .exec(deleteUser)
                .pause(DEFAULT_PAUSE)
                .exec(placeOrder)
                .pause(DEFAULT_PAUSE)
                .exec(viewOrder);
    }
}
