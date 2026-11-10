# Gatling Pet Store

Performance tests for [Swagger Pet Store](https://petstore.swagger.io) built with Gatling (Java DSL) and Gradle.

## Stack

| Tool    | Version |
|---------|---------|
| Java    | 25 (LTS, toolchain) |
| Gradle  | 9.9 (wrapper) |
| Gatling | 3.16 |

## Run

```bash
./gradlew :load-tests:gatlingRun
./gradlew :load-tests:gatlingRun -Dscenario=storeOrder -Dprofile=load
./gradlew :load-tests:gatlingRun -Denv=test -Dprofile=stress -Dprofiles.stress.steps=10
```

Report: `load-tests/build/reports/gatling/<simulation>-<timestamp>/index.html`

## Scenarios

| Key              | Steps |
|------------------|-------|
| `browsePets`     | randomSwitch 33/33/33: Find available / pending / sold pets → View found pet |
| `petLifecycle`   | Create pet (random tag from `pet_tags.csv`) → View pet → Delete pet → Check pet not exists |
| `userLifecycle`  | Create user → View user → Update user → View user → Delete user → Check user not exists |
| `storeOrder`     | Place order → View order |
| `petUserStore`   | Find available/pending/sold pets → Create user → View user → Update user → View user → Delete user → Place order → View order |

Run several in parallel: `-Dscenario=browsePets,userLifecycle,storeOrder`.

## Configuration

All settings live in `load-tests/src/gatling/resources/application.conf` (HOCON).
Any key can be overridden with `-D<key>=<value>`, e.g. `-Dprofiles.load.rate=20 -Dsla.p95Ms=800 -Dpause.min=1s -Dpause.max=3s`.

| Key        | Env var    | Values |
|------------|------------|--------|
| `scenario` | `SCENARIO` | key from `ScenarioMapping`, comma-separated to run several in parallel |
| `env`      | `ENV`      | `prod`, `test` |
| `profile`  | `PROFILE`  | `smoke`, `load`, `stress`, `spike`, `soak` |
| `httpLog`  | —          | `DEBUG` failed requests (default), `TRACE` all, `OFF` |

Each environment under `environments` defines `baseUrl` and `dataDir`.
`prod` and `test` currently share the same `baseUrl` and differ only in test data.

## Test data

Per-environment files live in `load-tests/src/gatling/resources/<dataDir>/`:

```
data/
├── prod/pet_tags.csv
└── test/pet_tags.csv
```

```java
feed(Feeders.csvFeeder("pet_tags.csv").random())
```

`Feeders` resolves the file from the current environment's `dataDir` and fails fast if it is missing.

## Structure

```
load-tests/src/gatling/java/petstore
├── api                      pure endpoints, one package per controller (pet, store, user)
│   └── <controller>
│       ├── <X>Requests      HttpRequestActionBuilder per endpoint, no checks
│       └── enums            values used in paths, query params and checks
├── steps                    <X>Steps: user action = group(name) over one or more requests + checks + extractors
├── testdata                 <X>Data: generated values put into the session for body templates
├── scenarios                user flows: scenario(name).exec(step).pause(...).exec(step), no groups
├── simulations              LoadSimulation, ScenarioMapping, SimulationExtended
└── common
    ├── config               TestConfig, LoadProfile, ConfigException
    ├── Assertions           SLA assertions
    ├── Feeders              per-env test data
    ├── Pauses               DEFAULT_PAUSE: random between `pause.min` and `pause.max`
    ├── Protocols            HTTP protocol
    └── Constants, SessionKeys

load-tests/src/gatling/resources
├── bodies                   JSON request bodies with #{sessionKey} placeholders (ElFileBody)
└── data/<env>               per-env CSV/JSON feeders
```

## Adding a scenario

1. `resources/bodies/<name>.json` — request body template, if the endpoint has a body.
2. `api/<controller>/<X>Requests` — endpoints (`...Rq`).
3. `testdata/<X>Data` — generate the values the template needs, put them into the session with `session.setAll(...)`.
4. `steps/<X>Steps` — `group(...).on(...).exitHereIfFailed()` with checks (`jsonPath`) and extracted data (`saveAs`).
5. `scenarios/<Name>Scenario` — `scenario(name).exec(step1).pause(DEFAULT_PAUSE).exec(step2)...`.
6. Register it in `ScenarioMapping` and run with `-Dscenario=<key>`.

Request names are `METHOD /path/{param}`; Gatling prefixes them with the group path in reports.
