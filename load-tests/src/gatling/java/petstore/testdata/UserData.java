package petstore.testdata;

import static petstore.common.SessionKeys.EMAIL;
import static petstore.common.SessionKeys.PHONE;
import static petstore.common.SessionKeys.USERNAME;
import static petstore.common.SessionKeys.USER_ID;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class UserData {

    private UserData() {}

    public static Map<String, Object> newUser() {
        var random = ThreadLocalRandom.current();
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        return Map.of(
                USER_ID, random.nextLong(1_000_000L, 9_000_000_000L),
                USERNAME, "perf_user_" + suffix,
                EMAIL, "perf_" + suffix + "@test.com",
                PHONE, "+380" + random.nextLong(100_000_000L, 999_999_999L));
    }

    public static String updatedUsername(String username) {
        return username + "_updated";
    }
}
