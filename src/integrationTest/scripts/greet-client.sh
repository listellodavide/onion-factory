#!/bin/bash

# Simple script to make GET requests to the greet endpoint
# Usage: ./greet-client.sh [name]
# If name is provided, it will make a request to /greet/{name}
# Otherwise, it will make a request to /greet

# Set the base URL
BASE_URL="http://localhost:8080"

# Check if a name parameter was provided
if [ -n "$1" ]; then
    # Make request with name parameter
    echo "Making request to $BASE_URL/greet/$1"
    curl -X GET "$BASE_URL/greet/$1"
else
    # Make request without name parameter
    echo "Making request to $BASE_URL/greet"
    curl -X GET "$BASE_URL/greet"
fi

# Add a newline after the response for better readability
echo ""