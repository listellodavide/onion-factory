# Java Greet Client

This is a Java implementation of the greet client that can be executed independently like a script.

## Files

- `GreetClient.java` - The Java client implementation
- `greet-client-java.sh` - Shell script wrapper to execute the Java client

## Usage

### Using the shell script wrapper

```bash
# Without a name parameter
./greet-client-java.sh

# With a name parameter
./greet-client-java.sh YourName
```

### Using Java directly

```bash
# Without a name parameter
java GreetClient.java

# With a name parameter
java GreetClient.java YourName
```

## How it works

The Java client uses the `HttpClient` API to make GET requests to the greet endpoint:

- If a name parameter is provided, it makes a request to `/greet/{name}`
- Otherwise, it makes a request to `/greet`

The client prints the response from the server to the console.

## Requirements

- Java 21 or higher (for the `HttpClient` API)
- The greet service must be running on `http://localhost:8080`

## Testing

You can use the provided test script to verify that the Java client works correctly:

```bash
./test-greet-client.sh
```

This will test the client both with and without a name parameter.

### Gradle Integration Test

You can also run the Java client as an integration test using Gradle:

```bash
./gradlew greetIntegrationTest
```

This Gradle task executes the greet-client-java.sh script in the correct working directory. Make sure the server is running on localhost:8080 before executing this task.