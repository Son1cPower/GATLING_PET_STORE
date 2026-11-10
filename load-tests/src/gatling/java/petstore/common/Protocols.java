package petstore.common;

import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.http.HttpProtocolBuilder;
import petstore.common.Constants.Headers;
import petstore.common.config.TestConfig;

public final class Protocols {

    private Protocols() {}

    public static HttpProtocolBuilder petStore(TestConfig config) {
        return http.baseUrl(config.baseUrl())
                .acceptHeader(Headers.APPLICATION_JSON)
                .acceptEncodingHeader("gzip, deflate")
                .userAgentHeader(Headers.USER_AGENT);
    }
}
