# Performance Testing Framework

## Overview

Load tests for [Swagger Pet Store](https://petstore.swagger.io) built with **Gatling** (Java DSL) and **Gradle**.
Environments, workloads and thresholds are set in config files and picked at run time.

---

## Installation

1. **Clone the repository.**

2. **Install Java 25** (any JDK distribution, e.g. Temurin). Gradle and Gatling come with the wrapper, nothing else to install.

```bash
java -version
```

3. **Check the build:**

```bash
./gradlew :load-tests:gatlingClasses
```

> On Windows use `.\gradlew.bat` instead of `./gradlew`.

---

## Running Tests

### Local Execution

**Smoke test (every scenario once):**

```bash
./gradlew :load-tests:gatlingRun
```

**One scenario with a workload:**

```bash
./gradlew :load-tests:gatlingRun -Dworkload=testLoad -Dscenario=storeOrder
```

**Several scenarios in parallel:**

```bash
./gradlew :load-tests:gatlingRun -Dworkload=baseLoad -Dscenario=browsePets,storeOrder
```

**Different environment and thresholds:**

```bash
./gradlew :load-tests:gatlingRun -Denv=test -Dworkload=baseLoad -Dthreshold=highLoad -Dscenario=browsePets,storeOrder
```

**Report:** `load-tests/build/reports/gatling/<simulation>-<timestamp>/index.html`
**Log file:** `load-tests/build/logs/gatling.log`


## Debug Mode

HTTP logging is set with `httpLog`:

**FULL DEBUG (all requests and responses with headers and body):**

```bash
./gradlew :load-tests:gatlingRun -Dscenario=storeOrder -DhttpLog=TRACE
```

**FAILED ONLY (default):**

```bash
./gradlew :load-tests:gatlingRun -Dscenario=storeOrder -DhttpLog=DEBUG
```

**OFF:**

```bash
./gradlew :load-tests:gatlingRun -Dscenario=storeOrder -DhttpLog=OFF
```

---

## Environment Variables

Set an option as an environment variable or as `-D<name>=<value>`. If both are set, `-D` wins.

| Variable    | `-D` option  | Description                                             | Example               |
| ----------- | ------------ | ------------------------------------------------------- | --------------------- |
| `WORKLOAD`  | `workload`   | Load model: smoke / testLoad / baseLoad / highLoad / baseLoadOpen / highLoadOpen | `baseLoad` |
| `SCENARIO`  | `scenario`   | Scenarios to run, comma-separated (empty = all in the workload) | `browsePets,storeOrder` |
| `ENV`       | `env`        | Target environment: prod / test                         | `test`                |
| `THRESHOLD` | `threshold`  | Threshold set (empty = same name as the workload)       | `highLoad`            |
| —           | `httpLog`    | HTTP logs: DEBUG / TRACE / OFF                          | `TRACE`               |

---

## Configuration

All settings live in [`load-tests/gatling-config`](load-tests/gatling-config):

| File                | What is inside                                         |
| ------------------- | ------------------------------------------------------ |
| `gatling.conf`      | Defaults: env, workload, pauses, max duration + Gatling settings |
| `environments.conf` | Base URL and test data folder per environment          |
| `workloads.conf`    | Load per workload and scenario                         |
| `thresholds.conf`   | Pass / fail limits                                     |
| `logback-test.xml`  | Logging                                                |

### Workloads

| Workload       | Model  | Load                                   |
| -------------- | ------ | -------------------------------------- |
| `smoke`        | —      | each scenario once                     |
| `testLoad`     | closed | 1 user per scenario for 1 min          |
| `baseLoad`     | closed | 10 users total, 1 min ramp + 10 min    |
| `highLoad`     | closed | 20 users total, 1 min ramp + 10 min    |
| `baseLoadOpen` | open   | 5 new users/s total, 1 min ramp + 10 min |
| `highLoadOpen` | open   | 10 new users/s total, 1 min ramp + 10 min |

Load for each scenario is written with Gatling steps:

```hocon
baseLoad {
  storeOrder = [ { rampConcurrentUsers = [0, 2], during = 1m }, { constantConcurrentUsers = 2, during = 10m } ]
}
```

**Closed** = fixed number of users at the same time. **Open** = new users arriving per second.

### Thresholds

`default` applies to every run; a block with the workload's name overrides it.

```hocon
default {
  global      = { p99Ms = 2000, maxFailedPercent = 1.0 }   # all requests together
  eachRequest = { p95Ms = 2000 }                           # every request separately
  storeOrder  = { "Place order" = { p95Ms = 1000 } }       # one business step
}
highLoad {
  eachRequest = { p95Ms = 3000 }
}
```

---

## Available Test Scenarios

| Scenario          | Description                                                              |
| ----------------- | ------------------------------------------------------------------------ |
| `browsePets`      | Find available / pending / sold pets → view a found pet                  |
| `petLifecycle`    | Create pet → view → delete → check it is gone                            |
| `storeOrder`      | Place order → view order                                                 |
| `userLifecycle`   | Create user → login → view → update → view → delete → check it is gone   |
| `userSession`     | Setup: create user + login · Test: view → update → view · Teardown: delete + logout |
| `userSessionLoop` | Like `userSession`, but the test part repeats for 1 min with one login (closed workloads only) |

Scenarios that create data clean up after themselves, even when a step fails.
