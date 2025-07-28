package com.supabase.tests;

import com.supabase.base.TestBase;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ProductsTests extends TestBase {

    private static String createdId;

    @Test
    public void createProducts() {
        String payload = readJsonFile("data/create_products.json");
        Response response = given()
                .body(payload)
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .extract().response();

        createdId = response.jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createProducts")
    public void getProducts() {
        given()
            .when()
                .get("/products/" + createdId)
            .then()
                .statusCode(200)
                .body("id", equalTo(createdId));
    }

    @Test(dependsOnMethods = "getProducts")
    public void updateProducts() {
        String updatedPayload = readJsonFile("data/update_products.json");
        given()
                .body(updatedPayload)
            .when()
                .patch("/products/" + createdId)
            .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updateProducts")
    public void deleteProducts() {
        given()
            .when()
                .delete("/products/" + createdId)
            .then()
                .statusCode(204);
    }
}