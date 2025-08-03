# Product Demo Client

This is a simple demo client that creates a sample product (Yellow Onion) and then fetches it from the API.

## Files

- `src/integrationTest/scripts/ProductDemo.java`: Java client that creates a sample onion product and then fetches it
- `src/integrationTest/scripts/product-demo.sh`: Shell script wrapper to run the Java client

## Usage

### Using the Shell Script

```bash
./src/integrationTest/scripts/product-demo.sh
```

### Running the Java File Directly

```bash
cd src/integrationTest/scripts
java ProductDemo.java
```

## What it Does

1. Creates a sample Yellow Onion product with the following properties:
   - Name: "Yellow Onion"
   - Description: "Fresh yellow onion, locally grown"
   - Price: 1.99
   - Quantity: 100

2. Extracts the ID of the created product from the response

3. Fetches the created product using the extracted ID

## Requirements

- Java 11 or higher
- The application server must be running on http://localhost:8080