package petstore.api.user;

import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.http.HttpRequestActionBuilder;
import petstore.common.SessionKeys;

public final class UserRequests {

    private static final String USER_PATH = "/user";
    private static final String USER_BODY = "bodies/user.json";

    private UserRequests() {}

    public static HttpRequestActionBuilder postUserRq() {
        return http("POST " + USER_PATH)
                .post(USER_PATH)
                .body(ElFileBody(USER_BODY))
                .asJson();
    }

    public static HttpRequestActionBuilder getUserByUsernameRq(String usernameKey) {
        return http("GET " + USER_PATH + "/{username}")
                .get(USER_PATH + "/" + SessionKeys.el(usernameKey));
    }

    public static HttpRequestActionBuilder putUserRq(String usernameKey) {
        return http("PUT " + USER_PATH + "/{username}")
                .put(USER_PATH + "/" + SessionKeys.el(usernameKey))
                .body(ElFileBody(USER_BODY))
                .asJson();
    }

    public static HttpRequestActionBuilder deleteUserRq(String usernameKey) {
        return http("DELETE " + USER_PATH + "/{username}")
                .delete(USER_PATH + "/" + SessionKeys.el(usernameKey));
    }
}
