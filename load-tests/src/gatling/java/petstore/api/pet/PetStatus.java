package petstore.api.pet;

import java.util.Locale;

public enum PetStatus {
    AVAILABLE,
    PENDING,
    SOLD;

    public String value() {
        return name().toLowerCase(Locale.ROOT);
    }
}
