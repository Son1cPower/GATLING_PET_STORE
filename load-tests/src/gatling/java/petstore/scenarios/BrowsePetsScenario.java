package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.percent;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.api.pet.PetStatus.AVAILABLE;
import static petstore.api.pet.PetStatus.PENDING;
import static petstore.api.pet.PetStatus.SOLD;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.PetSteps.findPets;
import static petstore.steps.PetSteps.viewFoundPet;

import io.gatling.javaapi.core.ScenarioBuilder;

public class BrowsePetsScenario {

    public ScenarioBuilder browsePetsScenario() {
        return scenario("Browse Pets")
                .randomSwitch().on(
                        percent(33.4).then(
                                exec(findPets(AVAILABLE))
                                        .pause(DEFAULT_PAUSE)
                                        .exec(viewFoundPet(AVAILABLE))),
                        percent(33.3).then(
                                exec(findPets(PENDING))
                                        .pause(DEFAULT_PAUSE)
                                        .exec(viewFoundPet(PENDING))),
                        percent(33.3).then(
                                exec(findPets(SOLD))
                                        .pause(DEFAULT_PAUSE)
                                        .exec(viewFoundPet(SOLD))));
    }
}
