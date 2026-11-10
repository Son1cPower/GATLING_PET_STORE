package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.UserSteps.checkUserNotExists;
import static petstore.steps.UserSteps.createUser;
import static petstore.steps.UserSteps.deleteUser;
import static petstore.steps.UserSteps.deleteUserIfCreated;
import static petstore.steps.UserSteps.login;
import static petstore.steps.UserSteps.logoutIfLoggedIn;
import static petstore.steps.UserSteps.updateUser;
import static petstore.steps.UserSteps.viewUser;

import io.gatling.javaapi.core.ScenarioBuilder;
import petstore.common.Flow;

public class UserLifecycleScenario {

    public ScenarioBuilder userLifecycleScenario() {
        return scenario("User Lifecycle").exec(Flow.withTeardown(
                exec(createUser)
                        .pause(DEFAULT_PAUSE)
                        .exec(login)
                        .pause(DEFAULT_PAUSE)
                        .exec(viewUser)
                        .pause(DEFAULT_PAUSE)
                        .exec(updateUser)
                        .pause(DEFAULT_PAUSE)
                        .exec(viewUser)
                        .pause(DEFAULT_PAUSE)
                        .exec(deleteUser)
                        .pause(DEFAULT_PAUSE)
                        .exec(checkUserNotExists),
                exec(deleteUserIfCreated).exec(logoutIfLoggedIn)));
    }
}
