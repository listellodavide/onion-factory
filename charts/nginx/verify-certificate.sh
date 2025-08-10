#!/bin/bash

# Apply the updated certificate and ingress configuration
kubectl apply -f charts/nginx/certificate.yaml
kubectl apply -f charts/nginx/myingress.yaml

# Update the Helm deployment
echo "Updating Helm deployment..."
helm upgrade --install demo-local-ai ./charts/demo-local-ai \
    -f ./charts/demo-local-ai/values-aks-dev.yaml \
    --namespace demo-local-ai \
    --create-namespace

echo "Waiting for certificate processing..."
sleep 15

# Check certificate status
echo "Certificate status:"
kubectl get certificate -n demo-local-ai

# Get detailed certificate information
echo "Certificate details:"
kubectl describe certificate -n demo-local-ai

# Check HTTP-01 challenges
echo "Challenge status:"
kubectl get challenges -n demo-local-ai

# Check secrets to verify TLS certificate creation
echo "Checking secrets:"
kubectl get secrets -n demo-local-ai | grep tls

# Test HTTPS connection (requires DNS to be set up)
echo "Testing HTTPS connection (if DNS is configured):"
echo "curl -k https://helloworlds.space"

echo "Note: If certificate is still not ready, you may need to wait longer for Let's Encrypt to issue the certificate."
echo "You can run this script again after a few minutes to check the status."