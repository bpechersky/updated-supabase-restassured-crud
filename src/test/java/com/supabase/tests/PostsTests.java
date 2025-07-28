package com.supabase.tests;

import com.supabase.base.TestBase;
import com.supabase.base.TestData;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostsTests extends TestBase {

    @Test
    public void createPosts() {
        String payload = readJsonFile("data/create_posts.json");

        Response response = given()
                .queryParam("apikey", apiKey)
                .contentType("application/json")
                .header("Prefer", "return=representation")
                .body(payload)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract().response();

        String postId = response.jsonPath().getString("[0].id");
        TestData.createdPostId = postId;
        System.out.println("✅ Post created: " + postId);
    }

    @Test(dependsOnMethods = "createPosts")
    public void getPosts() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdPostId)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(TestData.createdPostId));
    }

    @Test(dependsOnMethods = "getPosts")
    public void updatePosts() {
        String updatedPayload = readJsonFile("data/update_posts.json");

        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdPostId)
                .contentType("application/json")
                .body(updatedPayload)
                .when()
                .patch("/posts")
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "updatePosts")
    public void deletePosts() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdPostId)
                .when()
                .delete("/posts")
                .then()
                .statusCode(204);

    }
}
