#!/bin/bash

# Compile and run the SlugGenerationTest.java file
echo "Compiling and running SlugGenerationTest.java..."
cd "$(dirname "$0")"
javac SlugGenerationTest.java
java SlugGenerationTest