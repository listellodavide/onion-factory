import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java client for creating and fetching users.
 * This file can be executed directly as a script.
 * 
 * Usage: java CreateUsers.java
 * It will create a couple of sample users and then fetch them.
 * Error handling is implemented to only fetch users if creation was successful.
 */
public class CreateUsers {
    // Set the base URL
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();
        
        // Create and fetch first user (John Doe)
        createAndFetchUser(client, 
            "johndoe", 
            "password123", 
            "john.doe@example.com", 
            "John Doe");
        
        // Create and fetch second user (Jane Smith)
        createAndFetchUser(client, 
            "janesmith", 
            "securepass456", 
            "jane.smith@example.com", 
            "Jane Smith");
            
        // Create and fetch third user (Davide)
        createAndFetchUser(client, 
            "davide", 
            "davide123", 
            "davide@example.com", 
            "Davide");
            
        // Create and fetch fourth user (Inna)
        createAndFetchUser(client, 
            "inna2", 
            "inna456", 
            "inna@example.com",
            "Inna");
    }
    
    /**
     * Creates a user and fetches it only if creation was successful
     * 
     * @param client HTTP client to use for requests
     * @param username Username for the new user
     * @param password Password for the new user
     * @param email Email for the new user
     * @param displayName Display name for logging purposes
     */
    private static void createAndFetchUser(HttpClient client, String username, String password, 
                                          String email, String displayName) 
                                          throws IOException, InterruptedException {
        System.out.println("\nCreating user (" + displayName + ")...");
        
        // Create user JSON
        String userJson = "{"
            + "\"username\": \"" + username + "\","
            + "\"password\": \"" + password + "\","
            + "\"email\": \"" + email + "\""
            + "}";
        
        // Create HTTP request for creating the user
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        
        try {
            // Send request and get response
            HttpResponse<String> createUserResponse = client.send(createUserRequest, 
                                                               HttpResponse.BodyHandlers.ofString());
            
            // Print response
            System.out.println("Create User Response Status: " + createUserResponse.statusCode());
            System.out.println("Create User Response Body: " + createUserResponse.body());
            
            // Check if user creation was successful (2xx status code)
            if (createUserResponse.statusCode() >= 200 && createUserResponse.statusCode() < 300) {
                try {
                    // Extract the user ID from the response
                    String responseBody = createUserResponse.body();
                    
                    // Simple parsing to extract ID - assumes the ID is in the format "id":123
                    int idIndex = responseBody.indexOf("\"id\":");
                    if (idIndex == -1) {
                        System.out.println("Error: Could not find user ID in response");
                        return;
                    }
                    
                    int commaIndex = responseBody.indexOf(",", idIndex);
                    if (commaIndex == -1) {
                        // If no comma found, try to find closing brace
                        commaIndex = responseBody.indexOf("}", idIndex);
                        if (commaIndex == -1) {
                            System.out.println("Error: Malformed response, could not extract user ID");
                            return;
                        }
                    }
                    
                    String idStr = responseBody.substring(idIndex + 5, commaIndex).trim();
                    System.out.println("Extracted User ID: " + idStr);
                    
                    // Now fetch the created user
                    fetchUser(client, idStr);
                } catch (Exception e) {
                    System.out.println("Error extracting user ID: " + e.getMessage());
                }
            } else {
                System.out.println("User creation failed with status code: " + createUserResponse.statusCode());
                System.out.println("Not attempting to fetch user since creation failed");
            }
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }
    
    /**
     * Fetches a user by ID
     * 
     * @param client HTTP client to use for requests
     * @param userId ID of the user to fetch
     */
    private static void fetchUser(HttpClient client, String userId) throws IOException, InterruptedException {
        System.out.println("\nFetching the created user...");
        
        // Create HTTP request for fetching the user
        HttpRequest fetchUserRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .GET()
                .build();
        
        try {
            // Send request and get response
            HttpResponse<String> fetchUserResponse = client.send(fetchUserRequest, 
                                                              HttpResponse.BodyHandlers.ofString());
            
            // Print response
            System.out.println("Fetch User Response Status: " + fetchUserResponse.statusCode());
            System.out.println("Fetch User Response Body: " + fetchUserResponse.body());
            
            if (fetchUserResponse.statusCode() >= 400) {
                System.out.println("Error fetching user: HTTP " + fetchUserResponse.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
    }
}