package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.UserSteps.checkUserNotExists;
import static petstore.steps.UserSteps.createUser;
import static petstore.steps.UserSteps.deleteUser;
import static petstore.steps.UserSteps.updateUser;
import static petstore.steps.UserSteps.viewUser;

import io.gatling.javaapi.core.ScenarioBuilder;

public class UserLifecycleScenario {

    public ScenarioBuilder userLifecycleScenario() {
        return scenario("User Lifecycle")
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
                .exec(checkUserNotExists);
    }
}
