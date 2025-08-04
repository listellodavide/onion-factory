#!/bin/bash

# Compile the Java test file
echo "Compiling StripePaymentTest.java..."
javac StripePaymentTest.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful. Running test..."
    # Run the test
    java StripePaymentTest
else
    echo "Compilation failed. Please check the Java file for errors."
fi