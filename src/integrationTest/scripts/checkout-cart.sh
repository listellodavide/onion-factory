#!/bin/bash

# Script to compile and run the CheckoutCart Java client
# This script checks out the cart for user with ID 2

# Change to the scripts directory
cd "$(dirname "$0")"

# Compile the Java file
echo "Compiling CheckoutCart.java..."
javac CheckoutCart.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful. Running CheckoutCart..."
    # Run the Java program
    java CheckoutCart
else
    echo "Compilation failed."
fi