import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Integration test for Stripe webhook functionality.
 * This test simulates Stripe sending webhook events to our application.
 */
public class StripeWebhookTest {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws Exception {
        // Test checkout.session.completed webhook
        testCheckoutSessionCompletedWebhook();
        
        // Test payment_intent.succeeded webhook
        testPaymentIntentSucceededWebhook();
    }
    
    private static void testCheckoutSessionCompletedWebhook() throws Exception {
        System.out.println("[DEBUG_LOG] Testing checkout.session.completed webhook...");
        
        // Create a mock webhook event for checkout.session.completed
        String webhookJson = "{\n" +
                "  \"id\": \"evt_test_checkout_session_completed\",\n" +
                "  \"type\": \"checkout.session.completed\",\n" +
                "  \"data\": {\n" +
                "    \"object\": {\n" +
                "      \"id\": \"cs_test_123456789\",\n" +
                "      \"object\": \"checkout.session\",\n" +
                "      \"status\": \"complete\",\n" +
                "      \"metadata\": {\n" +
                "        \"orderId\": \"1\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/payments/webhook"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(webhookJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG_LOG] Webhook Response Status: " + response.statusCode());
        System.out.println("[DEBUG_LOG] Response: " + response.body());
        
        if (response.statusCode() == 200) {
            System.out.println("[DEBUG_LOG] Successfully processed checkout.session.completed webhook!");
        } else {
            System.out.println("[DEBUG_LOG] Failed to process webhook.");
        }
    }
    
    private static void testPaymentIntentSucceededWebhook() throws Exception {
        System.out.println("[DEBUG_LOG] Testing payment_intent.succeeded webhook...");
        
        // Create a mock webhook event for payment_intent.succeeded
        String webhookJson = "{\n" +
                "  \"id\": \"evt_test_payment_intent_succeeded\",\n" +
                "  \"type\": \"payment_intent.succeeded\",\n" +
                "  \"data\": {\n" +
                "    \"object\": {\n" +
                "      \"id\": \"pi_test_123456789\",\n" +
                "      \"object\": \"payment_intent\",\n" +
                "      \"status\": \"succeeded\",\n" +
                "      \"metadata\": {\n" +
                "        \"orderId\": \"1\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/payments/webhook"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(webhookJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG_LOG] Webhook Response Status: " + response.statusCode());
        System.out.println("[DEBUG_LOG] Response: " + response.body());
        
        if (response.statusCode() == 200) {
            System.out.println("[DEBUG_LOG] Successfully processed payment_intent.succeeded webhook!");
        } else {
            System.out.println("[DEBUG_LOG] Failed to process webhook.");
        }
    }
}