package petstore.steps;

import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.regex;
import static io.gatling.javaapi.http.HttpDsl.status;
import static petstore.api.user.UserRequests.deleteUserRq;
import static petstore.api.user.UserRequests.getUserByUsernameRq;
import static petstore.api.user.UserRequests.loginRq;
import static petstore.api.user.UserRequests.logoutRq;
import static petstore.api.user.UserRequests.postUserRq;
import static petstore.api.user.UserRequests.putUserRq;
import static petstore.common.SessionKeys.AUTH_TOKEN;
import static petstore.common.SessionKeys.OLD_USERNAME;
import static petstore.common.SessionKeys.PASSWORD;
import static petstore.common.SessionKeys.USERNAME;
import static petstore.common.SessionKeys.USER_CREATED;
import static petstore.common.SessionKeys.USER_ID;

import io.gatling.javaapi.core.ChainBuilder;
import petstore.common.Constants.Status;
import petstore.testdata.UserData;

public final class UserSteps {

    private UserSteps() {}

    public static final ChainBuilder createUser = group("Create user").on(
            exec(session -> session.setAll(UserData.newUser())),
            exec(postUserRq()
                    .check(status().is(Status.OK).saveAs(USER_CREATED))
                    .check(jsonPath("$.message").is(session -> String.valueOf(session.getLong(USER_ID))))));

    public static final ChainBuilder login = group("Login").on(
            exec(loginRq(USERNAME, PASSWORD)
                    .check(status().is(Status.OK))
                    .check(regex("logged in user session:(\\d+)").saveAs(AUTH_TOKEN))));

    public static final ChainBuilder viewUser = group("View user").on(
            exec(getUserByUsernameRq(USERNAME)
                    .check(status().is(Status.OK))
                    .check(jsonPath("$.username").is(session -> session.getString(USERNAME)))));

    public static final ChainBuilder updateUser = group("Update user").on(
            exec(session -> {
                var current = session.getString(USERNAME);
                return session.set(OLD_USERNAME, current).set(USERNAME, UserData.updatedUsername(current));
            }),
            exec(putUserRq(OLD_USERNAME).check(status().is(Status.OK))),
            exec(session -> session.remove(OLD_USERNAME)));

    public static final ChainBuilder deleteUser = group("Delete user").on(
            exec(session -> session.contains(OLD_USERNAME)
                    ? session.set(USERNAME, session.getString(OLD_USERNAME)).remove(OLD_USERNAME)
                    : session),
            exec(deleteUserRq(USERNAME).check(status().is(Status.OK))),
            exec(session -> session.remove(USER_CREATED)));

    public static final ChainBuilder deleteUserIfCreated =
            doIf(session -> session.contains(USER_CREATED)).then(deleteUser);

    public static final ChainBuilder logout = group("Logout").on(
            exec(logoutRq().check(status().is(Status.OK))),
            exec(session -> session.remove(AUTH_TOKEN)));

    public static final ChainBuilder logoutIfLoggedIn =
            doIf(session -> session.contains(AUTH_TOKEN)).then(logout);

    public static final ChainBuilder checkUserNotExists = group("Check user not exists").on(
            exec(getUserByUsernameRq(USERNAME)
                    .check(status().is(Status.NOT_FOUND))
                    .check(jsonPath("$.message").is("User not found"))));
}
