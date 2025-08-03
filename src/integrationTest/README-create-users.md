# Create Users Script

This document describes how to use the `CreateUsers.java` script to create sample users in the demo-local-ai application.

## Overview

The `CreateUsers.java` script is a Java client that demonstrates how to create and fetch users using the REST API of the demo-local-ai application. It creates two sample users:

1. John Doe (username: johndoe)
2. Jane Smith (username: janesmith)

After creating each user, it fetches the user details to verify the creation was successful.

## Prerequisites

- Java 11 or higher
- The demo-local-ai application running on localhost:8080

## Running the Script

You can run the script in two ways:

### Using the Shell Script

```bash
./src/integrationTest/scripts/create-users.sh
```

This will compile the Java file, run it, and clean up the class file afterward.

### Running the Java File Directly

```bash
cd src/integrationTest/scripts
javac CreateUsers.java
java CreateUsers
```

## Expected Output

The script will output the HTTP response status and body for each operation:

1. Creating the first user (John Doe)
2. Fetching the first user
3. Creating the second user (Jane Smith)
4. Fetching the second user

If successful, you should see HTTP status code 201 (Created) for the creation operations and 200 (OK) for the fetch operations.

## Troubleshooting

- If you get connection errors, make sure the demo-local-ai application is running on localhost:8080.
- If you get HTTP 400 errors, check that the user data format is correct.
- If you get HTTP 409 errors, the users might already exist in the database.