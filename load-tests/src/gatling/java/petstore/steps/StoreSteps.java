package petstore.steps;

import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.status;
import static petstore.api.store.StoreRequests.deleteOrderRq;
import static petstore.api.store.StoreRequests.getOrderByIdRq;
import static petstore.api.store.StoreRequests.postOrderRq;
import static petstore.common.SessionKeys.ORDER_CREATED;
import static petstore.common.SessionKeys.ORDER_ID;

import io.gatling.javaapi.core.ChainBuilder;
import petstore.api.store.OrderStatus;
import petstore.common.Constants.Status;
import petstore.testdata.OrderData;

public final class StoreSteps {

    private StoreSteps() {}

    public static final ChainBuilder placeOrder = group("Place order").on(
            exec(session -> session.setAll(OrderData.placedOrder())),
            exec(postOrderRq()
                    .check(status().is(Status.OK).saveAs(ORDER_CREATED))
                    .check(jsonPath("$.id").ofLong().is(session -> session.getLong(ORDER_ID)))));

    public static final ChainBuilder viewOrder = group("View order").on(
            exec(getOrderByIdRq(ORDER_ID)
                    .check(status().is(Status.OK))
                    .check(jsonPath("$.id").ofLong().is(session -> session.getLong(ORDER_ID)))
                    .check(jsonPath("$.status").is(OrderStatus.PLACED.value()))));

    public static final ChainBuilder deleteOrder = group("Delete order").on(
            exec(deleteOrderRq(ORDER_ID).check(status().is(Status.OK))),
            exec(session -> session.remove(ORDER_CREATED)));

    public static final ChainBuilder deleteOrderIfCreated =
            doIf(session -> session.contains(ORDER_CREATED)).then(deleteOrder);
}
