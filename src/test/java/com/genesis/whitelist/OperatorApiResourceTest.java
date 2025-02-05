package com.genesis.whitelist;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.builder.Json;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OperatorApiResourceTest {

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
            .statusCode(200)
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
            [
              "zzz"
            ]
            """;

        given()
            .contentType(ContentType.JSON)
            .body(jsonBody)
            .when()
            .post("/operator/xxx/ip")
            .then()
            .statusCode(200)
            .header("test", "addIps: xxx");
    }

}