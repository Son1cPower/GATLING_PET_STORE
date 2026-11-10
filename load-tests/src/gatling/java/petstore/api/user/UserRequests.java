package petstore.api.user;

import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.http.HttpRequestActionBuilder;
import petstore.common.Constants.Headers;
import petstore.common.SessionKeys;

public final class UserRequests {

    private static final String USER_PATH = "/user";
    private static final String USER_BODY = "bodies/user.json";
    private static final String BEARER = "Bearer " + SessionKeys.el(SessionKeys.AUTH_TOKEN);

    private UserRequests() {}

    public static HttpRequestActionBuilder postUserRq() {
        return http("POST " + USER_PATH)
                .post(USER_PATH)
                .body(ElFileBody(USER_BODY))
                .asJson();
    }

    public static HttpRequestActionBuilder loginRq(String usernameKey, String passwordKey) {
        return http("GET " + USER_PATH + "/login")
                .get(USER_PATH + "/login")
                .queryParam("username", SessionKeys.el(usernameKey))
                .queryParam("password", SessionKeys.el(passwordKey));
    }

    public static HttpRequestActionBuilder logoutRq() {
        return http("GET " + USER_PATH + "/logout")
                .get(USER_PATH + "/logout")
                .header(Headers.AUTHORIZATION, BEARER);
    }

    public static HttpRequestActionBuilder getUserByUsernameRq(String usernameKey) {
        return http("GET " + USER_PATH + "/{username}")
                .get(USER_PATH + "/" + SessionKeys.el(usernameKey));
    }

    public static HttpRequestActionBuilder putUserRq(String usernameKey) {
        return http("PUT " + USER_PATH + "/{username}")
                .put(USER_PATH + "/" + SessionKeys.el(usernameKey))
                .header(Headers.AUTHORIZATION, BEARER)
                .body(ElFileBody(USER_BODY))
                .asJson();
    }

    public static HttpRequestActionBuilder deleteUserRq(String usernameKey) {
        return http("DELETE " + USER_PATH + "/{username}")
                .delete(USER_PATH + "/" + SessionKeys.el(usernameKey))
                .header(Headers.AUTHORIZATION, BEARER);
    }
}
