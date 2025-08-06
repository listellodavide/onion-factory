#!/bin/bash

# Exit on error
set -e

echo "Deploying demo-local-ai to Kubernetes..."

# Create namespace
echo "Creating namespace..."
kubectl apply -f namespace.yaml

# Deploy configuration
echo "Deploying configuration..."
kubectl apply -f app-config.yaml
kubectl apply -f app-secrets.yaml


# Deploy PostgreSQL
echo "Deploying PostgreSQL..."
kubectl apply -f postgres-pvc.yaml
kubectl apply -f postgres.yaml


# Deploy application
echo "Deploying application..."
kubectl apply -f app.yaml
kubectl apply -f service.yaml

# Ask about ingress
read -p "Do you want to deploy the ingress resources? (y/n): " deploy_ingress
if [[ "$deploy_ingress" == "y" ]]; then
  echo "Deploying ingress..."
  kubectl apply -f ingress.yaml
  
  echo "You may need to add the following entry to your /etc/hosts file:"
  echo "127.0.0.1 demo-local-ai.local"
fi

echo "Deployment completed!"
echo "To check the status of your deployment, run:"
echo "kubectl get all -n demo-local-ai"