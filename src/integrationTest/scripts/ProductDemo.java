import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Java client for creating and fetching a product.
 * This file can be executed directly as a script.
 * 
 * Usage: java ProductDemo.java
 * It will create a sample onion product and then fetch it.
 */
public class ProductDemo {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // Create a sample onion product
        String productJson = "{"
            + "\"name\": \"Yellow Onion\","
            + "\"description\": \"Fresh yellow onion, locally grown\","
            + "\"price\": 1.99,"
            + "\"quantity\": 100"
            + "}";
        
        System.out.println("Creating a sample onion product...");
        
        // Create HTTP request for creating the product
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(productJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create Product Response Status: " + createResponse.statusCode());
        System.out.println("Create Product Response Body: " + createResponse.body());
        
        // Extract the product ID from the response
        String responseBody = createResponse.body();
        // Simple parsing to extract ID - assumes the ID is in the format "id":123
        int idIndex = responseBody.indexOf("\"id\":");
        int commaIndex = responseBody.indexOf(",", idIndex);
        String idStr = responseBody.substring(idIndex + 5, commaIndex);
        
        System.out.println("Extracted Product ID: " + idStr);
        
        // Now fetch the created product
        System.out.println("\nFetching the created product...");
        
        // Create HTTP request for fetching the product
        HttpRequest fetchRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products/" + idStr))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> fetchResponse = client.send(fetchRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Fetch Product Response Status: " + fetchResponse.statusCode());
        System.out.println("Fetch Product Response Body: " + fetchResponse.body());
        
        // Create a second onion product (Red Onion)
        System.out.println("\n\nCreating a second onion product...");
        String redOnionJson = "{"
            + "\"name\": \"Red Onion\","
            + "\"description\": \"Sweet red onion, perfect for salads\","
            + "\"price\": 2.49,"
            + "\"quantity\": 75"
            + "}";
        
        // Create HTTP request for creating the second product
        HttpRequest createRedOnionRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(redOnionJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> createRedOnionResponse = client.send(createRedOnionRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create Red Onion Response Status: " + createRedOnionResponse.statusCode());
        System.out.println("Create Red Onion Response Body: " + createRedOnionResponse.body());
        
        // Extract the product ID from the response
        String redOnionResponseBody = createRedOnionResponse.body();
        // Simple parsing to extract ID - assumes the ID is in the format "id":123
        int redOnionIdIndex = redOnionResponseBody.indexOf("\"id\":");
        int redOnionCommaIndex = redOnionResponseBody.indexOf(",", redOnionIdIndex);
        String redOnionIdStr = redOnionResponseBody.substring(redOnionIdIndex + 5, redOnionCommaIndex);
        
        System.out.println("Extracted Red Onion Product ID: " + redOnionIdStr);
        
        // Now fetch the created red onion product
        System.out.println("\nFetching the created red onion product...");
        
        // Create HTTP request for fetching the red onion product
        HttpRequest fetchRedOnionRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products/" + redOnionIdStr))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> fetchRedOnionResponse = client.send(fetchRedOnionRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Fetch Red Onion Response Status: " + fetchRedOnionResponse.statusCode());
        System.out.println("Fetch Red Onion Response Body: " + fetchRedOnionResponse.body());
    }
}