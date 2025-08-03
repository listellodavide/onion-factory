#!/bin/bash

# Script to run the AddCartItems.java client
# Usage: ./add-cart-items.sh
# This will add Yellow Onion and Red Onion products to the cart for user ID 2

# Navigate to the scripts directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Running AddCartItems.java to add items to cart for user ID 2..."
java AddCartItems.java

# Add a newline after the response for better readability
echo ""