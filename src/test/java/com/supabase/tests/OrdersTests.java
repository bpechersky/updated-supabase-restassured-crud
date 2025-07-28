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


    @Test
    public void createOrder() throws Exception {


        // ✅ Prepare payload with actual user/product IDs
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/data/create_orders.json")))
                .replace("sample-user-id", TestData.createdUserId)
                .replace("sample-product-id", TestData.createdProductId);

        // ✅ Create order (single attempt only)
        Response response = given()
                .queryParam("apikey", apiKey)
                .contentType(ContentType.JSON)
                .header("Prefer", "return=representation")
                .body(payload)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().response();

        // ✅ Extract and store order ID
        String orderId = response.jsonPath().getString("[0].id");
        TestData.createdOrderId = orderId;
        System.out.println("✅ Order created: " + orderId);
    }




    @Test(dependsOnMethods = "createOrder")
    public void getOrders() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdOrderId)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(TestData.createdOrderId));
    }


    @Test(dependsOnMethods = "getOrders")
    public void updateOrders() {
        String updatedPayload = readJsonFile("data/update_orders.json")
                .replace("sample-user-id", TestData.createdUserId)
                .replace("sample-product-id", TestData.createdProductId);

        System.out.println("🔄 PATCH payload: " + updatedPayload);

        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdOrderId)
                .contentType(ContentType.JSON)
                .body(updatedPayload)
                .when()
                .patch("/orders")
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updateOrders")
    public void verifyOrderUpdated() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdOrderId)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("[0].user_id", equalTo(TestData.createdUserId))
                .body("[0].product_id", equalTo(TestData.createdProductId))
                .body("[0].quantity", equalTo(1));
    }




}