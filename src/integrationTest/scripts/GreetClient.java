import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java client for making GET requests to the greet endpoint.
 * This file can be executed directly as a script.
 * 
 * Usage: java GreetClient.java [name]
 * If name is provided, it will make a request to /greet/{name}
 * Otherwise, it will make a request to /greet
 */
public class GreetClient {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // Build the URL based on whether a name parameter was provided
        String url;
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            // Make request with name parameter
            url = BASE_URL + "/greet/" + args[0];
            System.out.println("Making request to " + url);
        } else {
            // Make request without name parameter
            url = BASE_URL + "/greet";
            System.out.println("Making request to " + url);
        }
        
        // Create HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Print response
        System.out.println(response.body());
    }
}