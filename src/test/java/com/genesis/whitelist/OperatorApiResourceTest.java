package com.genesis.whitelist;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.model.UpdateIpsRequest;
import com.genesis.whitelist.model.UpdateIpsRequest.UpdateTypeEnum;
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
        assertEquals(3, response.body().as(ArrayList.class).size());
    }


    @Test
    void testAddOperatorFailAlreadyExists() {
        Operator toAdd = new Operator("EXISTING_OPERATOR");

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
        request.setUpdateType(UpdateTypeEnum.REMOVAL);
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