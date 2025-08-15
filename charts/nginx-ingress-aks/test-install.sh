#!/bin/bash
# Test script for nginx-ingress-aks chart installation

set -e

echo "=== Testing nginx-ingress-aks chart installation ==="

# Step 1: Update dependencies
echo "Updating dependencies..."
helm dependency update ./charts/nginx-ingress-aks

# Step 2: Validate the chart
echo "Validating chart..."
helm lint ./charts/nginx-ingress-aks

# Step 3: Template the chart to verify output
echo "Templating chart to verify output..."
helm template nginx-ingress-aks ./charts/nginx-ingress-aks > /tmp/nginx-ingress-aks-template.yaml

echo "=== Verification steps ==="
echo "1. Check if ingress-nginx is included:"
grep -q "ingress-nginx" /tmp/nginx-ingress-aks-template.yaml && echo "  ✅ ingress-nginx found" || echo "  ❌ ingress-nginx not found"

echo "2. Check if namespace is configured:"
grep -q "ingress-basic" /tmp/nginx-ingress-aks-template.yaml && echo "  ✅ namespace found" || echo "  ❌ namespace not found"

echo "=== Test completed ==="
echo "To install the chart on a real cluster, run:"
echo "helm install nginx-ingress-aks ./charts/nginx-ingress-aks"
echo ""
echo "After installation, get the external IP with:"
echo "kubectl get service -n ingress-basic ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}'"
echo ""
echo "Then configure your DNS to point execodex.com to this IP address."