import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java client for checking out a user's cart.
 * This file can be executed directly as a script.
 * 
 * Usage: java CheckoutCart.java
 * It will checkout the cart for user with ID 2, creating an order with all items in the cart.
 */
public class CheckoutCart {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    // Set the user ID
    private static final long USER_ID = 2;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // First, get the cart to see what items will be checked out
        System.out.println("Fetching the cart for user ID " + USER_ID + " before checkout...");
        getCart(client);
        
        // Checkout the cart
        System.out.println("\nChecking out the cart for user ID " + USER_ID + "...");
        checkoutCart(client);
        
        // Get the cart again to verify it was emptied after checkout
        System.out.println("\nFetching the cart after checkout to verify it was emptied...");
        getCart(client);
    }
    
    /**
     * Checks out the cart for the specified user.
     */
    private static void checkoutCart(HttpClient client) throws IOException, InterruptedException {
        // Create HTTP request for checking out the cart
        HttpRequest checkoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + USER_ID + "/cart/checkout"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        
        // Send request and get response
        HttpResponse<String> checkoutResponse = client.send(checkoutRequest, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println("Checkout Response Status: " + checkoutResponse.statusCode());
        System.out.println("Checkout Response Body: " + checkoutResponse.body());
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