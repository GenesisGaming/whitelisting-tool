package com.gn.whitelist;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gn.whitelist.model.Operator;
import com.gn.whitelist.model.UpdateIpsRequest;
import com.gn.whitelist.model.UpdateIpsRequest.UpdateTypeEnum;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OperatorApiResourceTest {

    @Test
    @TestSecurity(user = "testReadOnly", roles = {"read-user"})
    void testGetOperators() {
        Response response = given()
          .when().get("/operator")
          .then()
                .extract().response();

        assertEquals(200, response.getStatusCode());
        assertEquals(3, response.body().as(ArrayList.class).size());
    }


    @Test
    @TestSecurity(user = "testAdmin", roles = {"admin"})
    void testAddOperatorFailAlreadyExists() {
        Operator toAdd = new Operator("mockOperator");

        given()
                .contentType(ContentType.JSON)
                .body(toAdd)
                .when()
                .post("/operator")
                .then()
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("errorCode", equalTo("EXISTING_OPERATOR"))
            .body("errorMessage", equalTo("The new operator already exists in Git"));
    }


    @Test
    @TestSecurity(user = "testReadOnly", roles = {"read-user"})
    void testGetOperatorIpListFails() {
        given()
                .when().get("/operator/nonexisting/ip-list")
                .then()
                .statusCode(404)
                .extract().response();

    }


    @Test
    @TestSecurity(user = "testAdmin", roles = {"admin"})
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
    @TestSecurity(user = "testAdmin", roles = {"admin"})
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

    @Test
    void testNotAuthenticated() {
        Response response = given()
            .when().get("/operator")
            .then()
            .extract().response();

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testReadOnly", roles = {"read-user"})
    void testNotAllowedToPerformOperation() {
        Operator toAdd = new Operator("mockOperator");

        given()
            .contentType(ContentType.JSON)
            .body(toAdd)
            .when()
            .post("/operator")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_FORBIDDEN);
    }

}