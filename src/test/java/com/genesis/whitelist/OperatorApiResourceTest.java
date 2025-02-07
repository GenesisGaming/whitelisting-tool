package com.genesis.whitelist;

import static io.restassured.RestAssured.given;

import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import io.restassured.http.ContentType;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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



// A big journey test that validates adding a new partner, adding the IPs and then retrieving them.
    @Test
    void testAddIpsAndValidate() {
        Operator toAdd = new Operator("IT" + System.currentTimeMillis());
        AddIpsRequest request = new AddIpsRequest();
        request.setWhitelistType(AddIpsRequest.WhitelistTypeEnum.API);
        // adding 2 IPs
        request.setNewIps(List.of(
                "1.2.3.5",
                "1.2.3.5",
                "1.2.3.5",
                "1.2.3.6/31"
        ));

        given()
                .contentType(ContentType.JSON)
                .body(toAdd)
                .when()
                .post("/operator");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/operator/" +  toAdd.getCode() +"/ip")
                .then()
                .statusCode(200);

        int countIps = given()
                .when().get("/operator/" +  toAdd.getCode() +"/ip")
                .then()
                .extract()
                .response()
                .body()
                .as(ArrayList.class)
                .size();


        assertEquals(2, countIps);
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
                .when().get("/operator/nonexisting/ip")
                .then()
                .statusCode(404)
                .extract().response();

    }


    @Test
    void testAddIpsFailsForNonExistingPartner() {
        AddIpsRequest request = new AddIpsRequest();
        request.setWhitelistType(AddIpsRequest.WhitelistTypeEnum.API);
        request.setNewIps(List.of(
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.4",
                "1.2.3.5/31"
        ));


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/operator/nonexisting/ip")
                .then()
                .statusCode(404)
                .extract().response();

        // to validate error message
    }

}