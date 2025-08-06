# Demo Local AI

## Prerequisites
1. You need Java and Docker installed on your machine.
2. You also need to have the ai/smollm2 model downloaded and available in your Docker environment.
3. You need a test: STRIPE_API_SECRETKEY

## Getting Started
1. Clone the repository.
2. Run the following command to build the project:
   ```bash
   ./gradlew build
   ```
3. Start the application:
   ```bash
   ./gradlew bootRun
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

## API Documentation

The application includes Swagger/OpenAPI documentation for all REST endpoints. Once the application is running, you can access:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

The API documentation provides detailed information about all available endpoints, including:
- Request parameters
- Request bodies
- Response formats
- Response codes

## integration with Stripe
Add in your environment variables:
STRIPE_API_SECRETKEY

## Build an image
`./gradlew  bootBuildImage --no-publishImage --imageName=onion-factory:latest`
You can use your username/onion-factory:latest and skip the --no-publishImage to upload the image to your docker hub.
But don't be an idiot and upload the image with your secret key in it.
then try: 

`docker compose -f docker-compose.dev.yaml up`
to close:

`docker compose -f docker-compose.dev.yaml down -v`


## Troubleshooting
If you encounter issues, ensure that:
1. stop and remove all containers and volumes:
`docker compose down -v`
2. Or from volumes in docker desktop delete the volumes