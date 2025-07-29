package com.supabase.tests;

import com.supabase.base.TestBase;
import com.supabase.base.TestData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;



public class CommentsTests extends TestBase {

    @Test
    public void createComments() {
        String payload = readJsonFile("data/create_comments.json");

        System.out.println("📦 Payload: " + payload);

        Response response = given()
                .queryParam("apikey", apiKey)
                .contentType(ContentType.JSON)
                .header("Prefer", "return=representation")
                .body(payload)
                .when()
                .post("/comments")
                .then()
                .statusCode(201)
                .extract().response();

        String commentId = response.jsonPath().getString("[0].id");
        TestData.createdCommentId = commentId;
        System.out.println("✅ Comment created: " + commentId);
    }


    @Test(dependsOnMethods = "createComments")
    public void getComments() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdCommentId)
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(TestData.createdCommentId));
    }


    @Test(dependsOnMethods = "getComments")
    public void updateComments() {
        String updatedPayload = readJsonFile("data/update_comments.json");

        System.out.println("🔄 PATCH payload: " + updatedPayload);

        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdCommentId)
                .contentType(ContentType.JSON)
                .body(updatedPayload)
                .when()
                .patch("/comments")
                .then()
                .statusCode(204);
    }



    @Test(dependsOnMethods = "updateComments")
    public void deleteComments() {
        given()
                .queryParam("apikey", apiKey)
                .queryParam("id", "eq." + TestData.createdCommentId)
                .contentType(ContentType.JSON)
                .when()
                .delete("/comments") // ✅ No ID in the path
                .then()
                .statusCode(204)
                .log().all();

    }


}