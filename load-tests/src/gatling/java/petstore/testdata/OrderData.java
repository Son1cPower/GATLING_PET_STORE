package petstore.testdata;

import static petstore.common.SessionKeys.ORDER_ID;
import static petstore.common.SessionKeys.ORDER_PET_ID;
import static petstore.common.SessionKeys.QUANTITY;
import static petstore.common.SessionKeys.SHIP_DATE;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderData {

    private OrderData() {}

    public static Map<String, Object> placedOrder() {
        var random = ThreadLocalRandom.current();
        return Map.of(
                ORDER_ID, random.nextLong(1_000_000L, 9_000_000_000L),
                ORDER_PET_ID, random.nextLong(1, 10),
                QUANTITY, random.nextInt(1, 5),
                SHIP_DATE, Instant.now().truncatedTo(ChronoUnit.MILLIS).toString());
    }
}
