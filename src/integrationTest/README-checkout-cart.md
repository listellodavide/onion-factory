# Checkout Cart Script

This script demonstrates how to checkout a user's cart using the cart API.

## Overview

The script performs the following operations:
1. Fetches the current cart for user ID 2 to see what items will be checked out
2. Checks out the cart by making a POST request to the checkout endpoint
3. Fetches the cart again to verify it was emptied after checkout

## Prerequisites

- Java 11 or higher
- The demo-local-ai application must be running on localhost:8080
- User with ID 2 must exist
- The user's cart should have items in it (you can add items using the add-cart-items.sh script)

## Usage

### Using the Shell Script

```bash
./checkout-cart.sh
```

This will compile and run the Java client.

### Running the Java File Directly

```bash
cd src/integrationTest/scripts
javac CheckoutCart.java
java CheckoutCart
```

## API Endpoint

The script uses the following endpoint:

- `POST /users/{userId}/cart/checkout` - Checks out the user's cart, creating an order with all items in the cart

## Example Output

```
Fetching the cart for user ID 2 before checkout...
Get Cart Response Status: 200
Get Cart Response Body: {"id":2,"userId":2,"items":[{"id":1,"productId":1,"quantity":3,"productName":"Yellow Onion","productPrice":1.99},{"id":2,"productId":2,"quantity":2,"productName":"Red Onion","productPrice":2.49}]}

Checking out the cart for user ID 2...
Checkout Response Status: 201
Checkout Response Body: {"id":1,"userId":2,"items":[{"id":1,"productId":1,"quantity":3,"productName":"Yellow Onion","productPrice":1.99},{"id":2,"productId":2,"quantity":2,"productName":"Red Onion","productPrice":2.49}],"total":11.95,"status":"CREATED","createdAt":"2025-08-03T22:21:00Z"}

Fetching the cart after checkout to verify it was emptied...
Get Cart Response Status: 200
Get Cart Response Body: {"id":2,"userId":2,"items":[]}
```

## Notes

- The checkout operation will fail with a 400 Bad Request if the cart is empty
- After successful checkout, the cart is automatically emptied
- The response includes the created order with all items and the total price