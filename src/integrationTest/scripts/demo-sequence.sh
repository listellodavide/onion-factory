#!/bin/bash

# Script to run a sequence of demo scripts
# This script calls create-users.sh, product-demo.sh, and add-cart-items.sh in sequence

# Navigate to the scripts directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== Starting Demo Sequence ==="
echo ""

# Step 1: Create users
echo "=== Step 1: Creating Users ==="
./create-users.sh
echo ""

# Step 2: Run product demo
echo "=== Step 2: Running Product Demo ==="
./product-demo.sh
echo ""

# Step 3: Add items to cart
echo "=== Step 3: Adding Items to Cart ==="
./add-cart-items.sh
echo ""

echo "=== Demo Sequence Completed ==="
