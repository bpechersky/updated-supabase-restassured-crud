package com.supabase.tests;

import com.supabase.base.TestBase;
import com.supabase.base.TestData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.supabase.base.TestBase.readJsonFile;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.fail;

public class OrdersTests extends TestBase {

    private static String createdId;
    @BeforeClass
    public void waitBeforeOrders() throws InterruptedException {
        Thread.sleep(3000);
    }

    @Test
    public void createOrder() throws Exception {
        // 🔍 Check if user exists in DB before proceeding
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdUserId)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        // Read and update payload
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/data/create_order.json")))
                .replace("sample-user-id", TestData.createdUserId)
                .replace("sample-product-id", TestData.createdProductId);

        // 🔁 Retry logic for order creation
        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            Response response = given()
                    .queryParam("apikey", apiKey)
                    .contentType(ContentType.JSON)
                    .header("Prefer", "return=representation")
                    .body(payload)
                    .when()
                    .post("/orders");

            if (response.getStatusCode() == 201) {
                System.out.println("✅ Order created on attempt #" + (i + 1));
                String orderId = response.jsonPath().getString("[0].id");
                orderId = response.jsonPath().getString("[0].id");
                TestData.createdOrderId = orderId;
                break;
            }

            System.out.println("⚠️ Attempt #" + (i + 1) + " failed with " + response.getStatusCode());
            Thread.sleep(1000);

            if (i == maxRetries - 1) {
                fail("Order creation failed after retries: " + response.asPrettyString());
            }
        }
    }


    @Test(dependsOnMethods = "createOrder")
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