package petstore.common;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.jsonFile;
import static petstore.common.config.TestConfig.testConfig;

import io.gatling.javaapi.core.FeederBuilder;
import petstore.common.config.ConfigException;

public final class Feeders {

    private Feeders() {}

    public static FeederBuilder.SeparatedValues<String> csvFeeder(String fileName) {
        return csv(resolve(fileName));
    }

    public static FeederBuilder.FileBased<Object> jsonFeeder(String fileName) {
        return jsonFile(resolve(fileName));
    }

    private static String resolve(String fileName) {
        var config = testConfig();
        var path = "%s/%s".formatted(config.dataDir(), fileName);
        if (Feeders.class.getClassLoader().getResource(path) == null) {
            throw new ConfigException("Test data '%s' not found for env '%s'. Expected: src/gatling/resources/%s"
                    .formatted(fileName, config.env(), path));
        }
        return path;
    }
}
