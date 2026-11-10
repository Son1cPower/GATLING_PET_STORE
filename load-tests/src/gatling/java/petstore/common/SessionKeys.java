package petstore.common;

public final class SessionKeys {

    public static final String PET_ID = "petId";
    public static final String PET_NAME = "petName";
    public static final String PET_TAG = "tag";

    public static final String ORDER_ID = "orderId";
    public static final String ORDER_PET_ID = "orderPetId";
    public static final String QUANTITY = "quantity";
    public static final String SHIP_DATE = "shipDate";

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String OLD_USERNAME = "oldUsername";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String AUTH_TOKEN = "authToken";

    public static final String PET_CREATED = "petCreated";
    public static final String ORDER_CREATED = "orderCreated";
    public static final String USER_CREATED = "userCreated";

    private SessionKeys() {}

    public static String petId(String status) {
        return status + "PetId";
    }

    public static String el(String key) {
        return "#{" + key + "}";
    }
}
