
package com.supabase.base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestBase {

    protected String baseUrl;
    protected String apiKey;

    @BeforeClass
    public void setup() {
        baseUrl = System.getenv("SUPABASE_BASE_URL");
        apiKey = System.getenv("SUPABASE_API_KEY");

        if (baseUrl == null || apiKey == null) {
            throw new IllegalStateException("SUPABASE_BASE_URL or SUPABASE_API_KEY environment variable is not set.");
        }

        RestAssured.baseURI = baseUrl;
    }
    public static String readJsonFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get("src/test/resources/" + filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

}
