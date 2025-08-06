## Running Kubernetes Locally

### Prerequisites
1. Docker
2. kind (Kubernetes in Docker)
3. cloud-provider-kind
4. kubectl

go install sigs.k8s.io/cloud-provider-kind@latest

### Setting Up the Cluster
Run cloud-provider-kind
```bash
sudo cloud-provider-kind
```

```bash
# Create a kind cluster
kind create cluster --name dev1

# Get and set the kubeconfig
export KUBECONFIG="$(kind get kubeconfig --name=dev1)"

# Verify the cluster is running
kubectl get nodes
```

### Deploying the Application

1. Create the namespace:
```bash
kubectl apply -f namespace.yaml
```
2. Check status 
```bash
kubectl get all --namespace demo-local-ai
```

3. Create the application configuration:
```bash
kubectl apply -f app-config.yaml
kubectl apply -f app-secrets.yaml
```

2. Create the PostgreSQL resources:
```bash
kubectl apply -f postgres-pvc.yaml
kubectl apply -f postgres.yaml
```



4. Deploy the application:
```bash
kubectl apply -f app.yaml
```

5. Set up ingress (optional):
```bash
# Install NGINX Ingress Controller if not already installed
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml

# Apply the ingress resource
kubectl apply -f ingress.yaml

# Add the hostname to your /etc/hosts file
echo "127.0.0.1 demo-local-ai.local" | sudo tee -a /etc/hosts
```

### Verifying the Deployment

```bash
# Check all resources in the namespace
kubectl get all -n demo-local-ai

# Check the application logs
kubectl logs -n demo-local-ai deployment/demo-local-ai

# Port-forward to access the application directly (if not using ingress)
kubectl port-forward -n demo-local-ai svc/demo-local-ai 8080:8080
```

Then access the application at http://localhost:8080 or http://demo-local-ai.local if using ingress.
