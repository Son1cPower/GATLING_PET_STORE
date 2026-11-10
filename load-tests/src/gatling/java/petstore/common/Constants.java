package petstore.common;

public final class Constants {

    private Constants() {}

    public static final class Status {
        public static final int OK = 200;
        public static final int NOT_FOUND = 404;

        private Status() {}
    }

    public static final class Headers {
        public static final String APPLICATION_JSON = "application/json";
        public static final String USER_AGENT = "gatling-petstore-perf";
        public static final String AUTHORIZATION = "Authorization";

        private Headers() {}
    }
}
