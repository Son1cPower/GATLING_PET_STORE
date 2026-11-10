package petstore.api.pet;

import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.http.HttpRequestActionBuilder;
import petstore.common.SessionKeys;

public final class PetRequests {

    private static final String PET_PATH = "/pet";

    private PetRequests() {}

    public static HttpRequestActionBuilder findPetsByStatusRq(PetStatus status) {
        return http("GET " + PET_PATH + "/findByStatus?status=" + status.value())
                .get(PET_PATH + "/findByStatus")
                .queryParam("status", status.value());
    }

    public static HttpRequestActionBuilder getPetByIdRq(String petIdKey) {
        return http("GET " + PET_PATH + "/{petId}")
                .get(PET_PATH + "/" + SessionKeys.el(petIdKey));
    }

    public static HttpRequestActionBuilder postPetRq() {
        return http("POST " + PET_PATH)
                .post(PET_PATH)
                .body(ElFileBody("bodies/pet.json"))
                .asJson();
    }

    public static HttpRequestActionBuilder deletePetRq(String petIdKey) {
        return http("DELETE " + PET_PATH + "/{petId}")
                .delete(PET_PATH + "/" + SessionKeys.el(petIdKey));
    }
}
