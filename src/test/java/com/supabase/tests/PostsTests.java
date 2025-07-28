package com.supabase.tests;

import com.supabase.base.TestBase;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostsTests extends TestBase {

    private static String createdId;

    @Test
    public void createPosts() {
        String payload = readJsonFile("data/create_posts.json");
        Response response = given()
                .body(payload)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract().response();

        createdId = response.jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createPosts")
    public void getPosts() {
        given()
            .when()
                .get("/posts/" + createdId)
            .then()
                .statusCode(200)
                .body("id", equalTo(createdId));
    }

    @Test(dependsOnMethods = "getPosts")
    public void updatePosts() {
        String updatedPayload = readJsonFile("data/update_posts.json");
        given()
                .body(updatedPayload)
            .when()
                .patch("/posts/" + createdId)
            .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updatePosts")
    public void deletePosts() {
        given()
            .when()
                .delete("/posts/" + createdId)
            .then()
                .statusCode(204);
    }
}