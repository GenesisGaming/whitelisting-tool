package com.genesis.whitelist;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OperatorApiResourceTest {

    // TODO: Add response body validation to tests
    // TODO: Add red path scenarios 400, etc

    @Test
    void testGetOperators() {
        given()
          .when().get("/operator")
          .then()
             .statusCode(200)
            .header("test", "getOperators");
    }

    @Test
    void testAddOperators() {
        String jsonBody = """
            {
                "code": "xxx"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(jsonBody)
            .when()
            .post("/operator")
            .then()
            .statusCode(201)
            .header("test", "addOperator: xxx");
    }

    @Test
    void testGetOperatorIpList() {
        given()
            .when().get("/operator/xxx/ip")
            .then()
            .statusCode(200)
            .header("test", "getOperatorIpList: xxx");
    }

    @Test
    void testAddIps() {
        String jsonBody = """
            {
              "whitelistType": "API",
              "newIps": [
                "1.2.3.4",
                "1.2.3.5/31"
              ]
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(jsonBody)
            .when()
            .post("/operator/xxx/ip")

            .then()
            .statusCode(200)
            .header("test", "addIps : xxx IPs: [1.2.3.4, 1.2.3.5/31]");
    }

}