import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Integration test for Stripe payment functionality.
 * This test creates an order and then attempts to create a payment intent for it.
 */
public class StripePaymentTest {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws Exception {
        // Step 1: Create a user (if needed)
        String userId = createUser();
        System.out.println("[DEBUG_LOG] Created user with ID: " + userId);

        // Step 2: Create an order
        String orderId = createOrder(userId);
        System.out.println("[DEBUG_LOG] Created order with ID: " + orderId);

        // Step 3: Create a payment intent for the order
        createPaymentIntent(orderId);
        
        // Step 4: Create a checkout session for the order
        createCheckoutSession(orderId);
    }

    private static String createUser() throws Exception {
        String createUserJson = "{\"username\":\"stripe_test_user\",\"email\":\"stripe_test@example.com\",\"firstName\":\"Stripe\",\"lastName\":\"Test\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createUserJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            // Extract user ID from response
            String responseBody = response.body();
            // Simple extraction - in a real test, use a JSON parser
            int idIndex = responseBody.indexOf("\"id\":");
            if (idIndex != -1) {
                String idSubstring = responseBody.substring(idIndex + 5);
                int commaIndex = idSubstring.indexOf(",");
                if (commaIndex != -1) {
                    return idSubstring.substring(0, commaIndex).trim();
                }
            }
            System.out.println("[DEBUG_LOG] User created but couldn't extract ID. Response: " + responseBody);
            return "1"; // Fallback to default ID
        } else {
            System.out.println("[DEBUG_LOG] Failed to create user. Status: " + response.statusCode() + ", Response: " + response.body());
            return "1"; // Fallback to default ID
        }
    }

    private static String createOrder(String userId) throws Exception {
        String createOrderJson = "{" +
                "\"username\":\"stripe_test_user\"," +
                "\"items\":[" +
                "{\"productId\":1,\"quantity\":2}," +
                "{\"productId\":2,\"quantity\":1}" +
                "]" +
                "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createOrderJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            // Extract order ID from response
            String responseBody = response.body();
            // Simple extraction - in a real test, use a JSON parser
            int idIndex = responseBody.indexOf("\"id\":");
            if (idIndex != -1) {
                String idSubstring = responseBody.substring(idIndex + 5);
                int commaIndex = idSubstring.indexOf(",");
                if (commaIndex != -1) {
                    return idSubstring.substring(0, commaIndex).trim();
                }
            }
            System.out.println("[DEBUG_LOG] Order created but couldn't extract ID. Response: " + responseBody);
            return "1"; // Fallback to default ID
        } else {
            System.out.println("[DEBUG_LOG] Failed to create order. Status: " + response.statusCode() + ", Response: " + response.body());
            return "1"; // Fallback to default ID
        }
    }

    private static void createPaymentIntent(String orderId) throws Exception {
        String createPaymentIntentJson = "{\"orderId\":" + orderId + "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/payments/create-intent"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createPaymentIntentJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG_LOG] Create Payment Intent Status: " + response.statusCode());
        System.out.println("[DEBUG_LOG] Response: " + response.body());
        
        if (response.statusCode() == 200) {
            System.out.println("[DEBUG_LOG] Successfully created payment intent!");
            
            // In a real application, you would use the client secret to confirm the payment
            // on the client side using Stripe.js or a mobile SDK
            System.out.println("[DEBUG_LOG] Payment flow test completed successfully.");
        } else {
            System.out.println("[DEBUG_LOG] Failed to create payment intent.");
        }
    }
    
    private static void createCheckoutSession(String orderId) throws Exception {
        String createCheckoutSessionJson = "{" +
                "\"orderId\":" + orderId + "," +
                "\"successUrl\":\"https://example.com/success\"," +
                "\"cancelUrl\":\"https://example.com/cancel\"" +
                "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/payments/create-checkout"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createCheckoutSessionJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG_LOG] Create Checkout Session Status: " + response.statusCode());
        System.out.println("[DEBUG_LOG] Response: " + response.body());
        
        if (response.statusCode() == 200) {
            System.out.println("[DEBUG_LOG] Successfully created checkout session!");
            
            // In a real application, you would redirect the user to the checkout URL
            System.out.println("[DEBUG_LOG] Checkout session flow test completed successfully.");
        } else {
            System.out.println("[DEBUG_LOG] Failed to create checkout session.");
        }
    }
}