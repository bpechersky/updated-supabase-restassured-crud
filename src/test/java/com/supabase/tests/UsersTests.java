package com.supabase.tests;

import com.google.gson.JsonObject;
import com.supabase.base.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsersTests extends TestBase {

    private static String userId;

    @Test
    public void createUser() throws Exception {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/data/create_user.json")));

        Response response = given()
                .queryParam("apikey", apiKey)
                .contentType(ContentType.JSON)
                .header("Prefer", "return=representation")
                .body(payload)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("username", notNullValue())
                .extract().response();

        System.out.println("Full createUser response:");
        System.out.println(response.asPrettyString());

        // Try default extraction first
        userId = response.jsonPath().getString("[0].id"); // ✅ Get the first user's ID


        // If needed, fallback to nested extraction (uncomment below if previous line gives null):
        // userId = response.jsonPath().getString("id.id");

        // Print userId for debugging
        System.out.println("Extracted userId: " + userId);
    }


    @Test(dependsOnMethods = "createUser")
    public void getUserById() {
        System.out.println("Calling GET /users?id=eq." + userId); // Print the full URL being called

        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + userId)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].email", equalTo("bp@mail.com"))
                .body("[0].name", equalTo("Boris"));
    }


    @Test(dependsOnMethods = "getUserById")
    public void updateUser() {
        JsonObject updated = new JsonObject();
        updated.addProperty("name", "Boris Updated");
        updated.addProperty("role", "Admin");

        given()
                .queryParam("apikey", apiKey)
                .contentType(ContentType.JSON)
                .body(updated.toString())
                .when()
                .patch("/users?id=eq." + userId)
                .then()
                .statusCode(204);
    }
    @Test(dependsOnMethods = "updateUser")
    public void verifyUpdatedUser() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + userId)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Boris Updated"))
                .body("[0].role", equalTo("Admin"));
    }


    @Test(dependsOnMethods = "verifyUpdatedUser")
    public void deleteUser() {
        given()
                .queryParam("apikey", apiKey)
                .when()
                .delete("/users?id=eq." + userId)
                .then()
                .statusCode(204);
    }
}
