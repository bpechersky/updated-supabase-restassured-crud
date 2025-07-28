package com.supabase.tests;

import com.supabase.base.TestBase;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.UUID;

import static com.supabase.base.TestBase.readJsonFile;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrdersTests extends TestBase {

    private static String createdId;

    @Test
    public void createOrders() {
        String payload = readJsonFile("data/create_orders.json");
        Response response = given()
                .body(payload)
            .when()
                .post("/orders")
            .then()
                .statusCode(201)
                .extract().response();

        createdId = response.jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createOrders")
    public void getOrders() {
        given()
            .when()
                .get("/orders/" + createdId)
            .then()
                .statusCode(200)
                .body("id", equalTo(createdId));
    }

    @Test(dependsOnMethods = "getOrders")
    public void updateOrders() {
        String updatedPayload = readJsonFile("data/update_orders.json");
        given()
                .body(updatedPayload)
            .when()
                .patch("/orders/" + createdId)
            .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updateOrders")
    public void deleteOrders() {
        given()
            .when()
                .delete("/orders/" + createdId)
            .then()
                .statusCode(204);
    }
}