package com.supabase.tests;

import com.supabase.base.TestBase;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;



public class CommentsTests extends TestBase {

    private static String createdId;

    @Test
    public void createComments() {
        String payload = readJsonFile("data/create_comments.json");
        Response response = given()
                .body(payload)
            .when()
                .post("/comments")
            .then()
                .statusCode(201)
                .extract().response();

        createdId = response.jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createComments")
    public void getComments() {
        given()
            .when()
                .get("/comments/" + createdId)
            .then()
                .statusCode(200)
                .body("id", equalTo(createdId));
    }

    @Test(dependsOnMethods = "getComments")
    public void updateComments() {
        String updatedPayload = readJsonFile("data/update_comments.json");
        given()
                .body(updatedPayload)
            .when()
                .patch("/comments/" + createdId)
            .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updateComments")
    public void deleteComments() {
        given()
            .when()
                .delete("/comments/" + createdId)
            .then()
                .statusCode(204);
    }
}