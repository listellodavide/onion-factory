#!/bin/bash

# Script to run the ProductDemo.java client
# Usage: ./product-demo.sh
# This will create a sample onion product and then fetch it

# Navigate to the scripts directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Running ProductDemo.java to create and fetch a sample onion product..."
java ProductDemo.java

# Add a newline after the response for better readability
echo ""