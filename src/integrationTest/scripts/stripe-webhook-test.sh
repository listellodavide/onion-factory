#!/bin/bash

# Compile the test class
echo "Compiling StripeWebhookTest.java..."
javac StripeWebhookTest.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful. Running test..."
    # Run the test
    java StripeWebhookTest
else
    echo "Compilation failed."
fi