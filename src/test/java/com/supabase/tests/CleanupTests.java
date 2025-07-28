package com.supabase.tests;

import com.supabase.base.TestData;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.AfterSuite;

public class CleanupTests {
    String apiKey = System.getenv("SUPABASE_API_KEY"); // or hardcoded for now

    @AfterSuite
    public void cleanup() {
        if (TestData.createdOrderId != null) {
            given()
                    .queryParam("apikey", apiKey)
                    .delete("/orders?id=eq." + TestData.createdOrderId)
                    .then()
                    .statusCode(anyOf(is(200), is(204)));
        }

        if (TestData.createdProductId != null) {
            given()
                    .queryParam("apikey", apiKey)
                    .delete("/products?id=eq." + TestData.createdProductId)
                    .then()
                    .statusCode(anyOf(is(200), is(204)));
        }

        if (TestData.createdUserId != null) {
            given()
                    .queryParam("apikey", apiKey)
                    .delete("/users?id=eq." + TestData.createdUserId)
                    .then()
                    .statusCode(anyOf(is(200), is(204)));
        }
    }
}
