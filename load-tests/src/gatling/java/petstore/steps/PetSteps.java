package petstore.steps;

import static io.gatling.javaapi.core.CoreDsl.doIf;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.status;
import static petstore.api.pet.PetRequests.deletePetRq;
import static petstore.api.pet.PetRequests.findPetsByStatusRq;
import static petstore.api.pet.PetRequests.getPetByIdRq;
import static petstore.api.pet.PetRequests.postPetRq;
import static petstore.common.SessionKeys.PET_CREATED;
import static petstore.common.SessionKeys.PET_ID;
import static petstore.common.SessionKeys.PET_TAG;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import petstore.api.pet.PetStatus;
import petstore.common.Constants.Status;
import petstore.common.Feeders;
import petstore.common.SessionKeys;
import petstore.testdata.PetData;

public final class PetSteps {

    private PetSteps() {}

    private static final FeederBuilder<String> PET_TAGS = Feeders.csvFeeder("pet_tags.csv").random();

    public static ChainBuilder findPets(PetStatus status) {
        return group("Find " + status.value() + " pets").on(
                exec(findPetsByStatusRq(status)
                        .check(status().is(Status.OK))
                        .check(jsonPath("$[*].id").ofLong().findRandom().saveAs(SessionKeys.petId(status.value())))));
    }

    public static ChainBuilder viewFoundPet(PetStatus status) {
        var petIdKey = SessionKeys.petId(status.value());
        return group("View " + status.value() + " pet").on(
                exec(getPetByIdRq(petIdKey)
                        .check(status().is(Status.OK))
                        .check(jsonPath("$.id").ofLong().is(session -> session.getLong(petIdKey)))
                        .check(jsonPath("$.status").is(status.value()))));
    }

    public static final ChainBuilder createPet = group("Create pet").on(
            feed(PET_TAGS),
            exec(session -> session.setAll(PetData.newPet())),
            exec(postPetRq()
                    .check(status().is(Status.OK).saveAs(PET_CREATED))
                    .check(jsonPath("$.id").ofLong().is(session -> session.getLong(PET_ID)))
                    .check(jsonPath("$.tags[0].name").is(session -> session.getString(PET_TAG)))));

    public static final ChainBuilder viewPet = group("View pet").on(
            exec(getPetByIdRq(PET_ID)
                    .check(status().is(Status.OK))
                    .check(jsonPath("$.id").ofLong().is(session -> session.getLong(PET_ID)))
                    .check(jsonPath("$.tags[0].name").is(session -> session.getString(PET_TAG)))));

    public static final ChainBuilder deletePet = group("Delete pet").on(
            exec(deletePetRq(PET_ID).check(status().is(Status.OK))),
            exec(session -> session.remove(PET_CREATED)));

    public static final ChainBuilder deletePetIfCreated =
            doIf(session -> session.contains(PET_CREATED)).then(deletePet);

    public static final ChainBuilder checkPetNotExists = group("Check pet not exists").on(
            exec(getPetByIdRq(PET_ID)
                    .check(status().is(Status.NOT_FOUND))
                    .check(jsonPath("$.message").is("Pet not found"))));
}
