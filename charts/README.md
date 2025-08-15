# Helm Charts for Demo Local AI

This directory contains Helm charts for deploying the Demo Local AI application and its infrastructure on Kubernetes.

## Available Charts

### Application Chart

- **demo-local-ai**: Deploys the Demo Local AI application with its dependencies

### Infrastructure Charts

- **nginx-ingress-aks**: Installs the NGINX Ingress Controller on AKS (Step 1 of TLS setup)

## TLS Setup for AKS

To set up TLS for your application on AKS, you need to follow a three-step process:

### Step 1: Install NGINX Ingress Controller

```bash
# Update dependencies
helm dependency update ./charts/nginx-ingress-aks

# Install the chart
helm install nginx-ingress-aks ./charts/nginx-ingress-aks
```

After installation, get the external IP of your ingress controller:

```bash
kubectl get service -n ingress-basic ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
```

### Manual Step: Configure DNS

Configure your DNS to point your domain (e.g., execodex.com) to the external IP address of your ingress controller in Azure DNS zone. Wait for DNS propagation to complete.

You can verify DNS propagation with:

```bash
nslookup execodex.com
```

### Step 2: Install Certificate Manager

```bash
# Install cert-manager
helm upgrade --install cert-manager jetstack/cert-manager \
  -n cert-manager \
  --version v1.18.2 \
  --set crds.enabled=true \
  --set startupapicheck.enabled=false
```

### Step 3: Deploy the Demo Local AI Application

```bash
helm install demo-local-ai ./charts/demo-local-ai  --namespace demo-local-ai --create-namespace
# Install the demo-local-ai application
helm install demo-local-ai ./charts/demo-local-ai -f charts/demo-local-ai/values-aks-dev.yaml --namespace demo-local-ai --create-namespace
```

You should now be able to access your application securely at https://execodex.com

