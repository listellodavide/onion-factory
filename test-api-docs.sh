#!/bin/bash

echo "Testing API docs endpoint..."
curl -v http://localhost:8080/api-docs

echo -e "\n\nTesting Swagger UI redirect..."
curl -v -L http://localhost:8080/swagger-ui.html