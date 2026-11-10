package petstore.api.store;

import java.util.Locale;

public enum OrderStatus {
    PLACED,
    APPROVED,
    DELIVERED;

    public String value() {
        return name().toLowerCase(Locale.ROOT);
    }
}
