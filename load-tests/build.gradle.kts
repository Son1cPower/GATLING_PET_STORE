plugins {
    java
    alias(libs.plugins.gatling)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    named("gatling") {
        resources.srcDir("gatling-config")
    }
}

dependencies {
    gatlingImplementation(libs.typesafe.config)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all,-this-escape", "-Werror"))
}

gatling {
    jvmArgs = listOf(
        "-server",
        "-Xms512M",
        "-Xmx2G",
        "-XX:+UseZGC",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED",
    )
    systemProperties = mapOf("file.encoding" to "UTF-8")
}
