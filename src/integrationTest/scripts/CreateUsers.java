import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

/**
 * Java client for creating and fetching users.
 * This file can be executed directly as a script.
 * 
 * Usage: java CreateUsers.java
 * It will create a couple of sample users and then fetch them.
 */
public class CreateUsers {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // Create first user (John Doe)
        String johnDoeJson = "{"
            + "\"username\": \"johndoe\","
            + "\"password\": \"password123\","
            + "\"email\": \"john.doe@example.com\""
            + "}";
        
        System.out.println("Creating first user (John Doe)...");
        
        // Create HTTP request for creating the first user
        HttpRequest createJohnRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(johnDoeJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> createJohnResponse = client.send(createJohnRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create User Response Status: " + createJohnResponse.statusCode());
        System.out.println("Create User Response Body: " + createJohnResponse.body());
        
        // Extract the user ID from the response
        String johnResponseBody = createJohnResponse.body();
        // Simple parsing to extract ID - assumes the ID is in the format "id":123
        int johnIdIndex = johnResponseBody.indexOf("\"id\":");
        int johnCommaIndex = johnResponseBody.indexOf(",", johnIdIndex);
        String johnIdStr = johnResponseBody.substring(johnIdIndex + 5, johnCommaIndex);
        
        System.out.println("Extracted User ID: " + johnIdStr);
        
        // Now fetch the created user
        System.out.println("\nFetching the created user...");
        
        // Create HTTP request for fetching the user
        HttpRequest fetchJohnRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + johnIdStr))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> fetchJohnResponse = client.send(fetchJohnRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Fetch User Response Status: " + fetchJohnResponse.statusCode());
        System.out.println("Fetch User Response Body: " + fetchJohnResponse.body());
        
        // Create second user (Jane Smith)
        System.out.println("\n\nCreating second user (Jane Smith)...");
        String janeSmithJson = "{"
            + "\"username\": \"janesmith\","
            + "\"password\": \"securepass456\","
            + "\"email\": \"jane.smith@example.com\""
            + "}";
        
        // Create HTTP request for creating the second user
        HttpRequest createJaneRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(janeSmithJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> createJaneResponse = client.send(createJaneRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create User Response Status: " + createJaneResponse.statusCode());
        System.out.println("Create User Response Body: " + createJaneResponse.body());
        
        // Extract the user ID from the response
        String janeResponseBody = createJaneResponse.body();
        // Simple parsing to extract ID - assumes the ID is in the format "id":123
        int janeIdIndex = janeResponseBody.indexOf("\"id\":");
        int janeCommaIndex = janeResponseBody.indexOf(",", janeIdIndex);
        String janeIdStr = janeResponseBody.substring(janeIdIndex + 5, janeCommaIndex);
        
        System.out.println("Extracted User ID: " + janeIdStr);
        
        // Now fetch the created user
        System.out.println("\nFetching the created user...");
        
        // Create HTTP request for fetching the user
        HttpRequest fetchJaneRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + janeIdStr))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> fetchJaneResponse = client.send(fetchJaneRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Fetch User Response Status: " + fetchJaneResponse.statusCode());
        System.out.println("Fetch User Response Body: " + fetchJaneResponse.body());
    }
}