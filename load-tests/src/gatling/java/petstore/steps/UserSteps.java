package petstore.steps;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.status;
import static petstore.api.user.UserRequests.deleteUserRq;
import static petstore.api.user.UserRequests.getUserByUsernameRq;
import static petstore.api.user.UserRequests.postUserRq;
import static petstore.api.user.UserRequests.putUserRq;
import static petstore.common.SessionKeys.OLD_USERNAME;
import static petstore.common.SessionKeys.USERNAME;
import static petstore.common.SessionKeys.USER_ID;

import io.gatling.javaapi.core.ChainBuilder;
import petstore.common.Constants.Status;
import petstore.testdata.UserData;

public final class UserSteps {

    private UserSteps() {}

    public static final ChainBuilder createUser = group("Create user").on(
            exec(session -> session.setAll(UserData.newUser())),
            exec(postUserRq()
                    .check(status().is(Status.OK))
                    .check(jsonPath("$.message").is(session -> String.valueOf(session.getLong(USER_ID)))))).exitHereIfFailed();

    public static final ChainBuilder viewUser = group("View user").on(
            exec(getUserByUsernameRq(USERNAME)
                    .check(status().is(Status.OK))
                    .check(jsonPath("$.username").is(session -> session.getString(USERNAME))))).exitHereIfFailed();

    public static final ChainBuilder updateUser = group("Update user").on(
            exec(session -> {
                var current = session.getString(USERNAME);
                return session.set(OLD_USERNAME, current).set(USERNAME, UserData.updatedUsername(current));
            }),
            exec(putUserRq(OLD_USERNAME).check(status().is(Status.OK))),
            exec(session -> session.remove(OLD_USERNAME))).exitHereIfFailed();

    public static final ChainBuilder deleteUser = group("Delete user").on(
            exec(deleteUserRq(USERNAME).check(status().is(Status.OK)))).exitHereIfFailed();

    public static final ChainBuilder checkUserNotExists = group("Check user not exists").on(
            exec(getUserByUsernameRq(USERNAME)
                    .check(status().is(Status.NOT_FOUND))
                    .check(jsonPath("$.message").is("User not found")))).exitHereIfFailed();
}
