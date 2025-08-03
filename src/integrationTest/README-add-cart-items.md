# Add Cart Items Script

This script adds items to the cart for a user with ID 2.

## Files

- `AddCartItems.java` - The Java client implementation
- `add-cart-items.sh` - Shell script wrapper to execute the Java client

## Usage

### Using the shell script wrapper

```bash
./add-cart-items.sh
```

### Using Java directly

```bash
java AddCartItems.java
```

## How it works

The Java client performs the following operations:

1. Creates two products if they don't exist:
   - Yellow Onion (product ID 1)
   - Red Onion (product ID 2)

2. Adds these products to the cart for user ID 2:
   - 3 Yellow Onions
   - 2 Red Onions

3. Fetches the cart to verify the items were added successfully

The client uses the `HttpClient` API to make HTTP requests to the cart endpoints:
- POST to `/products` to create products
- POST to `/users/2/cart` to add items to the cart
- GET to `/users/2/cart` to fetch the cart

## Requirements

- Java 21 or higher (for the `HttpClient` API)
- The application must be running on `http://localhost:8080`
- User with ID 2 must exist (can be created using the `create-users.sh` script)

## Testing

Before running this script, make sure:
1. The application is running
2. User with ID 2 exists (run `./create-users.sh` first if needed)

Then run:
```bash
./add-cart-items.sh
```

The script will output the results of each operation, including the HTTP status codes and response bodies.