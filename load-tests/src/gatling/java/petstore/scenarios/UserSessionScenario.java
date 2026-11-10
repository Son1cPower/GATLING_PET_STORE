package petstore.scenarios;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static petstore.common.Pauses.DEFAULT_PAUSE;
import static petstore.steps.UserSteps.createUser;
import static petstore.steps.UserSteps.deleteUserIfCreated;
import static petstore.steps.UserSteps.login;
import static petstore.steps.UserSteps.logoutIfLoggedIn;
import static petstore.steps.UserSteps.updateUser;
import static petstore.steps.UserSteps.viewUser;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import petstore.common.Flow;

public class UserSessionScenario {

    public ScenarioBuilder userSessionScenario() {
        return scenario("User Session").exec(Flow.of(setup(), test(), teardown()));
    }

    private ChainBuilder setup() {
        return exec(createUser)
                .pause(DEFAULT_PAUSE)
                .exec(login)
                .pause(DEFAULT_PAUSE);
    }

    private ChainBuilder test() {
        return exec(viewUser)
                .pause(DEFAULT_PAUSE)
                .exec(updateUser)
                .pause(DEFAULT_PAUSE)
                .exec(viewUser)
                .pause(DEFAULT_PAUSE);
    }

    private ChainBuilder teardown() {
        return exec(deleteUserIfCreated).exec(logoutIfLoggedIn);
    }
}
