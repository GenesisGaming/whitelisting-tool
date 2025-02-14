package com.gn.whitelist;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gn.whitelist.model.Operator;
import com.gn.whitelist.model.UpdateIpsRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
class OperatorApiResourceIT extends OperatorApiResourceTest {
    // Execute the same tests but in packaged mode.

    // A big journey test that validates adding a new partner, adding the IPs, removing a couple and then validating the final list.
    // TODO decide if we want to have this kind of tests against a real Git repo. Currently, this test is not working against the mock.
    @Test
    void testUpdateIpsAndValidate() {
        Operator toAdd = new Operator("IT" + System.currentTimeMillis());

        given()
            .contentType(ContentType.JSON)
            .body(toAdd)
            .when()
            .post("/operator")
            .then()
            .statusCode(201);

        // add IPs request
        UpdateIpsRequest addIpsRequest = new UpdateIpsRequest();
        addIpsRequest.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        addIpsRequest.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.ADDITION);
        // adding 4 IPs with deduplication
        addIpsRequest.setIps(List.of(
            "1.2.3.5",
            "1.2.3.5",
            "1.5.5.9",
            "1.5.5.9",
            "4.4.4.4",
            "1.2.3.6/31"
        ));

        given()
            .contentType(ContentType.JSON)
            .body(addIpsRequest)
            .when()
            .patch("/operator/" +  toAdd.getCode() +"/ip-list")
            .then()
            .statusCode(200);

        // remove IPs request
        UpdateIpsRequest removeIpsRequest = new UpdateIpsRequest();
        removeIpsRequest.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        removeIpsRequest.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.REMOVAL);
        // removing 2 IPs
        removeIpsRequest.setIps(List.of(
            "1.2.3.5",
            "1.2.3.5",
            "1.2.3.6/31"
        ));

        given()
            .contentType(ContentType.JSON)
            .body(removeIpsRequest)
            .when()
            .patch("/operator/" +  toAdd.getCode() +"/ip-list")
            .then()
            .statusCode(200);

        // checking the IPs list after addition and removal
        @SuppressWarnings("rawtypes")
        ArrayList updatedIps = given()
            .when().get("/operator/" +  toAdd.getCode() +"/ip-list")
            .then()
            .extract()
            .response()
            .body()
            .as(ArrayList.class);

        //noinspection unchecked
        assertTrue(updatedIps.containsAll(List.of("1.5.5.9", "4.4.4.4")));
        assertEquals(2, updatedIps.size());
    }

}
