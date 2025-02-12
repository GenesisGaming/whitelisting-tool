package com.genesis.whitelist;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.model.UpdateIpsRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OperatorApiResourceTest {
    // TODO: validate also the response body of error messages


    @Test
    void testGetOperators() {
        Response response = given()
          .when().get("/operator")
          .then()
                .extract().response();

        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().as(ArrayList.class).size() > 10);
    }



// A big journey test that validates adding a new partner, adding the IPs, removing a couple and then validating the final list.
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


    @Test
    void testAddOperatorFailAlreadyExists() {
        Operator toAdd = new Operator("IT" + System.currentTimeMillis());

        given()
                .contentType(ContentType.JSON)
                .body(toAdd)
                .when()
                .post("/operator");

        given()
                .contentType(ContentType.JSON)
                .body(toAdd)
                .when()
                .post("/operator")
                .then()
                .statusCode(400);
    }


    @Test
    void testGetOperatorIpListFails() {
        given()
                .when().get("/operator/nonexisting/ip-list")
                .then()
                .statusCode(404)
                .extract().response();

    }


    @Test
    void testAddIpsFailsForNonExistingPartner() {
        UpdateIpsRequest request = new UpdateIpsRequest();
        request.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        request.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.ADDITION);
        request.setIps(List.of(
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.5/31"
        ));


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/operator/nonexisting/ip-list")
                .then()
                .statusCode(404)
                .extract().response();

        // to validate error message
    }


    @Test
    void testRemoveIpsFailsForNonExistingPartner() {
        UpdateIpsRequest request = new UpdateIpsRequest();
        request.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        request.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.ADDITION);
        request.setIps(List.of(
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.5/31"
        ));


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/operator/nonexisting/ip-list")
                .then()
                .statusCode(404)
                .extract().response();

        // to validate error message
    }

}