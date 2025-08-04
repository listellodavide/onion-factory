import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java client for testing product error handling.
 * This file can be executed directly as a script.
 * 
 * Usage: java ProductErrorTest.java
 * It will create a product and then attempt to create another product with the same SKU
 * to test the error handling.
 */
public class ProductErrorTest {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // Create a sample product
        String productJson = "{"
            + "\"sku\": \"TEST-SKU-001\","
            + "\"name\": \"Test Product\","
            + "\"description\": \"A test product for error handling\","
            + "\"price\": 9.99,"
            + "\"quantity\": 10"
            + "}";
        
        System.out.println("Creating a test product...");
        
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
        
        // Now try to create another product with the same SKU
        System.out.println("\nAttempting to create another product with the same SKU...");
        
        String duplicateProductJson = "{"
            + "\"sku\": \"TEST-SKU-001\","  // Same SKU as the first product
            + "\"name\": \"Duplicate SKU Product\","
            + "\"description\": \"This product has a duplicate SKU\","
            + "\"price\": 19.99,"
            + "\"quantity\": 5"
            + "}";
        
        // Create HTTP request for creating the duplicate product
        HttpRequest duplicateRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(duplicateProductJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> duplicateResponse = client.send(duplicateRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Duplicate Product Response Status: " + duplicateResponse.statusCode());
        System.out.println("Duplicate Product Response Body: " + duplicateResponse.body());
        
        // Verify that the error response has the expected status code and message
        if (duplicateResponse.statusCode() == 409) {
            System.out.println("\nSUCCESS: Received expected 409 Conflict status code for duplicate SKU");
            
            if (duplicateResponse.body().contains("Product Already Exists")) {
                System.out.println("SUCCESS: Error response contains 'Product Already Exists' message");
            } else {
                System.out.println("FAILURE: Error response does not contain expected message");
            }
        } else {
            System.out.println("\nFAILURE: Did not receive expected 409 Conflict status code");
            System.out.println("Received status code: " + duplicateResponse.statusCode());
        }
    }
}