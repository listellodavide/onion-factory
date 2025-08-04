import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Test for unique slug generation.
 * This test creates multiple products with the same name to verify that unique slugs are generated.
 * 
 * Usage: java SlugGenerationTest.java
 */
public class SlugGenerationTest {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        System.out.println("Testing unique slug generation for products with the same name...");
        
        // Create first product
        String product1Json = "{"
            + "\"sku\": \"TEST-PROD-001\","
            + "\"name\": \"Test Product\","
            + "\"description\": \"Test product for slug generation\","
            + "\"price\": 9.99,"
            + "\"quantity\": 10"
            + "}";
        
        // Create HTTP request for creating the first product
        HttpRequest createRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(product1Json))
                .build();
        
        // Send request and get response
        HttpResponse<String> createResponse1 = client.send(createRequest1, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create First Product Response Status: " + createResponse1.statusCode());
        System.out.println("Create First Product Response Body: " + createResponse1.body());
        
        // Extract the slug from the response
        String slug1 = extractSlug(createResponse1.body());
        System.out.println("First Product Slug: " + slug1);
        
        // Create second product with the same name but different SKU
        String product2Json = "{"
            + "\"sku\": \"TEST-PROD-002\","
            + "\"name\": \"Test Product\","
            + "\"description\": \"Second test product with the same name\","
            + "\"price\": 19.99,"
            + "\"quantity\": 5"
            + "}";
        
        // Create HTTP request for creating the second product
        HttpRequest createRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(product2Json))
                .build();
        
        // Send request and get response
        HttpResponse<String> createResponse2 = client.send(createRequest2, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("\nCreate Second Product Response Status: " + createResponse2.statusCode());
        System.out.println("Create Second Product Response Body: " + createResponse2.body());
        
        // Extract the slug from the response
        String slug2 = extractSlug(createResponse2.body());
        System.out.println("Second Product Slug: " + slug2);
        
        // Create third product with the same name but different SKU
        String product3Json = "{"
            + "\"sku\": \"TEST-PROD-003\","
            + "\"name\": \"Test Product\","
            + "\"description\": \"Third test product with the same name\","
            + "\"price\": 29.99,"
            + "\"quantity\": 3"
            + "}";
        
        // Create HTTP request for creating the third product
        HttpRequest createRequest3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(product3Json))
                .build();
        
        // Send request and get response
        HttpResponse<String> createResponse3 = client.send(createRequest3, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("\nCreate Third Product Response Status: " + createResponse3.statusCode());
        System.out.println("Create Third Product Response Body: " + createResponse3.body());
        
        // Extract the slug from the response
        String slug3 = extractSlug(createResponse3.body());
        System.out.println("Third Product Slug: " + slug3);
        
        // Verify that all slugs are different
        System.out.println("\nVerifying that all slugs are unique:");
        System.out.println("Slug 1: " + slug1);
        System.out.println("Slug 2: " + slug2);
        System.out.println("Slug 3: " + slug3);
        
        if (!slug1.equals(slug2) && !slug1.equals(slug3) && !slug2.equals(slug3)) {
            System.out.println("SUCCESS: All slugs are unique!");
        } else {
            System.out.println("FAILURE: Some slugs are not unique!");
        }
    }
    
    /**
     * Extract the slug from the JSON response.
     * 
     * @param jsonResponse the JSON response string
     * @return the extracted slug
     */
    private static String extractSlug(String jsonResponse) {
        if (jsonResponse.contains("\"slug\":")) {
            int slugIndex = jsonResponse.indexOf("\"slug\":");
            int slugStartIndex = jsonResponse.indexOf("\"", slugIndex + 7) + 1;
            int slugEndIndex = jsonResponse.indexOf("\"", slugStartIndex);
            return jsonResponse.substring(slugStartIndex, slugEndIndex);
        } else {
            return "SLUG_NOT_FOUND";
        }
    }
}