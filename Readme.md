1. You need Java and Docker installed on your machine.
2. You also need to have the ai/smollm2 model downloaded and available in your Docker environment.
3. Clone the repository.
4. Run the following command to build the project:
   ```bash
   ./gradlew build
   ```

## Integration Testing

You can run the Java client integration test using the Gradle task:

```bash
./gradlew greetIntegrationTest
```

This task executes the greet-client-java.sh script to test the greet endpoint. Make sure the server is running before executing this task.

## Using the Greet Client Script

The repository includes a shell script to interact with the server's greeting endpoint:

1. Make sure the server is running on localhost:8080
2. Use the script to make GET requests to the greet endpoint:
   ```bash
   # Basic greeting
   ./greet-client.sh
   
   # Greeting with a name parameter
   ./greet-client.sh YourName
   ```

## Troubleshooting
If you encounter issues, ensure that:
1. stop and remove all containers and volumes:
`docker compose down -v`
