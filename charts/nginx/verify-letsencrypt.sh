#!/bin/bash
# Script to verify Let's Encrypt certificate issuance

echo "Checking certificate status..."

# Apply the updated ingress configuration
echo "Applying updated ingress configuration..."
kubectl apply -f charts/nginx/myingress.yaml

# Wait for certificate to be processed
echo "Waiting for certificate to be processed (this may take a few minutes)..."
sleep 30

# Check certificate status
echo "Checking certificate status:"
kubectl get certificate -n demo-local-ai

# Check certificate details
echo "Certificate details:"
kubectl describe certificate demo-local-ai-tls-cert -n demo-local-ai

# Check challenges
echo "Certificate challenges:"
kubectl get challenges -n demo-local-ai

# Check cert-manager logs for any issues
echo "Cert-manager logs (last 50 lines):"
kubectl logs -n cert-manager -l app=cert-manager --tail=50

echo ""
echo "If the certificate is still showing as 'Kubernetes Ingress Controller Fake Certificate',"
echo "you may need to delete the existing certificate secret and let cert-manager recreate it:"
echo ""
echo "kubectl delete secret demo-local-ai-tls-cert -n demo-local-ai"
echo ""
echo "After deleting the secret, wait a few minutes and check the certificate status again."
echo ""
echo "To verify the certificate in your browser, visit https://helloworlds.space and check the certificate details."