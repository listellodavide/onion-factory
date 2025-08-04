#!/bin/bash

# Navigate to the scripts directory
cd "$(dirname "$0")"

# Compile the Java file
echo "Compiling ProductErrorTest.java..."
javac ProductErrorTest.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful. Running the test..."
    # Run the compiled Java class
    java ProductErrorTest
else
    echo "Compilation failed."
fi