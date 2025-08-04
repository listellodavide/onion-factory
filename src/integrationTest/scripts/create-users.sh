#!/bin/bash

# Navigate to the scripts directory
cd "$(dirname "$0")"

# Compile and run the CreateUsers.java script
#javac CreateUsers.java && java CreateUsers
java CreateUsers.java

# Clean up the class file
rm -f CreateUsers.class