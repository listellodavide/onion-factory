import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java client for adding items to a user's cart.
 * This file can be executed directly as a script.
 * 
 * Usage: java AddCartItems.java
 * It will add Yellow Onion and Red Onion products to the cart of user with ID 2.
 */
public class AddCartItems {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    // Set the user ID
    private static final long USER_ID = 2;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // First, create the products if they don't exist
//        createProducts(client);
        
        // Add Yellow Onion to cart
        System.out.println("\nAdding Yellow Onion to cart for user ID " + USER_ID + "...");
        addItemToCart(client, 1, 3); // Product ID 1, Quantity 3
        
        // Add Red Onion to cart
        System.out.println("\nAdding Red Onion to cart for user ID " + USER_ID + "...");
        addItemToCart(client, 2, 2); // Product ID 2, Quantity 2
        
        // Get the cart to verify items were added
        System.out.println("\nFetching the cart to verify items were added...");
        getCart(client);
    }
    
    /**
     * Creates Yellow Onion and Red Onion products if they don't exist.
     */
    private static void createProducts(HttpClient client) throws IOException, InterruptedException {
        // Create Yellow Onion product
        String yellowOnionJson = "{"
            + "\"name\": \"Yellow Onion\","
            + "\"description\": \"Fresh yellow onion, locally grown\","
            + "\"price\": 1.99,"
            + "\"quantity\": 100"
            + "}";
        
        System.out.println("Creating Yellow Onion product...");
        
        // Create HTTP request for creating the product
        HttpRequest createYellowOnionRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(yellowOnionJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> createYellowOnionResponse = client.send(createYellowOnionRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Create Yellow Onion Response Status: " + createYellowOnionResponse.statusCode());
        System.out.println("Create Yellow Onion Response Body: " + createYellowOnionResponse.body());
        
        // Create Red Onion product
        String redOnionJson = "{"
            + "\"name\": \"Red Onion\","
            + "\"description\": \"Sweet red onion, perfect for salads\","
            + "\"price\": 2.49,"
            + "\"quantity\": 75"
            + "}";
        
        System.out.println("\nCreating Red Onion product...");
        
        // Create HTTP request for creating the product
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
    }
    
    /**
     * Adds an item to the cart for the specified user.
     */
    private static void addItemToCart(HttpClient client, long productId, int quantity) throws IOException, InterruptedException {
        // Create request JSON
        String addCartItemJson = "{"
            + "\"productId\": " + productId + ","
            + "\"quantity\": " + quantity
            + "}";
        
        // Create HTTP request for adding item to cart
        HttpRequest addCartItemRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + USER_ID + "/cart/items"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(addCartItemJson))
                .build();
        
        // Send request and get response
        HttpResponse<String> addCartItemResponse = client.send(addCartItemRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Add Cart Item Response Status: " + addCartItemResponse.statusCode());
        System.out.println("Add Cart Item Response Body: " + addCartItemResponse.body());
    }
    
    /**
     * Gets the cart for the specified user.
     */
    private static void getCart(HttpClient client) throws IOException, InterruptedException {
        // Create HTTP request for getting the cart
        HttpRequest getCartRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + USER_ID + "/cart"))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> getCartResponse = client.send(getCartRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Get Cart Response Status: " + getCartResponse.statusCode());
        System.out.println("Get Cart Response Body: " + getCartResponse.body());
    }
}