package petstore.testdata;

import static petstore.common.SessionKeys.PET_ID;
import static petstore.common.SessionKeys.PET_NAME;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PetData {

    private PetData() {}

    public static Map<String, Object> newPet() {
        return Map.of(
                PET_ID, ThreadLocalRandom.current().nextLong(1_000_000L, 9_000_000_000L),
                PET_NAME, "perf_pet_" + UUID.randomUUID().toString().substring(0, 8));
    }
}
