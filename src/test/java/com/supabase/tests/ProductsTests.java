package com.supabase.tests;

import com.supabase.base.TestBase;
import com.supabase.base.TestData;
import io.restassured.http.ContentType;
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
                .queryParam("apikey", apiKey)
                .contentType(ContentType.JSON)
                .header("Prefer", "return=representation")
                .body(payload)
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .extract().response();

        createdId = response.jsonPath().getString("[0].id");
        TestData.createdProductId = createdId; // <-- Store globally
    }

    @Test(dependsOnMethods = "createProducts")
    public void getProducts() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + createdId)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(createdId));
    }


    @Test(dependsOnMethods = "getProducts")
    public void updateProducts() {
        String updatedPayload = readJsonFile("data/update_products.json");
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + createdId)
                .contentType(ContentType.JSON)
                .header("Prefer", "return=representation")
                .body(updatedPayload)
                .when()
                .patch("/products")
                .then()
                .statusCode(200);
    }




}