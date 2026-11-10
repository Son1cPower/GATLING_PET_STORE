package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.PetSteps.checkPetNotExists;
import static petstore.steps.PetSteps.createPet;
import static petstore.steps.PetSteps.deletePet;
import static petstore.steps.PetSteps.viewPet;

import io.gatling.javaapi.core.ScenarioBuilder;

public class PetLifecycleScenario {

    public ScenarioBuilder petLifecycleScenario() {
        return scenario("Pet Lifecycle")
                .exec(createPet)
                .pause(DEFAULT_PAUSE)
                .exec(viewPet)
                .pause(DEFAULT_PAUSE)
                .exec(deletePet)
                .pause(DEFAULT_PAUSE)
                .exec(checkPetNotExists);
    }
}
