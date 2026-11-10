package petstore.api.store;

import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.http.HttpRequestActionBuilder;
import petstore.common.SessionKeys;

public final class StoreRequests {

    private static final String ORDER_PATH = "/store/order";

    private StoreRequests() {}

    public static HttpRequestActionBuilder postOrderRq() {
        return http("POST " + ORDER_PATH)
                .post(ORDER_PATH)
                .body(ElFileBody("bodies/order.json"))
                .asJson();
    }

    public static HttpRequestActionBuilder getOrderByIdRq(String orderIdKey) {
        return http("GET " + ORDER_PATH + "/{orderId}")
                .get(ORDER_PATH + "/" + SessionKeys.el(orderIdKey));
    }

    public static HttpRequestActionBuilder deleteOrderRq(String orderIdKey) {
        return http("DELETE " + ORDER_PATH + "/{orderId}")
                .delete(ORDER_PATH + "/" + SessionKeys.el(orderIdKey));
    }
}
