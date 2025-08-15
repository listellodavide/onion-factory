# TLS Helm Chart

This Helm chart installs and configures cert-manager with Let's Encrypt for TLS certificate management in Kubernetes.

## Features

- Installs cert-manager with appropriate configuration
- Creates a ClusterIssuer for Let's Encrypt
- Configurable for both production and staging Let's Encrypt environments
- Supports HTTP-01 challenge for certificate validation

## Prerequisites

- Kubernetes 1.16+
- Helm 3.0+
- An ingress controller (e.g., nginx-ingress) installed in your cluster
- DNS configured for your domain

## Installation

```bash
# Add the Jetstack repository for cert-manager
helm repo add jetstack https://charts.jetstack.io
helm repo update

# Install the TLS chart
helm install tls-aks ./charts/tls -n tls-system --create-namespace
```

## Configuration

The following table lists the configurable parameters of the TLS chart and their default values.

| Parameter | Description | Default |
|-----------|-------------|---------|
| `certManager.enabled` | Enable cert-manager installation | `true` |
| `certManager.installCRDs` | Install cert-manager CRDs | `true` |
| `certManager.startupapicheck.enabled` | Enable startup API check | `false` |
| `clusterIssuer.name` | Name of the ClusterIssuer | `letsencrypt-prod` |
| `clusterIssuer.server` | Let's Encrypt server URL | `https://acme-v02.api.letsencrypt.org/directory` |
| `clusterIssuer.email` | Email address for Let's Encrypt | `admin@example.com` |
| `namespace.name` | Namespace for TLS resources | `tls-system` |
| `namespace.create` | Create the namespace | `true` |
| `domain` | Domain for certificates | `example.com` |

### Using the Staging Environment

For testing purposes, it's recommended to use the Let's Encrypt staging environment to avoid rate limits. Update the `clusterIssuer.server` value:

```yaml
clusterIssuer:
  server: https://acme-staging-v02.api.letsencrypt.org/directory
```

## Usage

After installing the chart, you can create Certificate resources that reference the ClusterIssuer:

```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: example-tls-cert
  namespace: your-namespace
spec:
  secretName: example-tls-cert
  issuerRef:
    name: letsencrypt-prod  # Must match clusterIssuer.name
    kind: ClusterIssuer
  commonName: example.com
  dnsNames:
  - example.com
  - www.example.com
```

## Verification

To verify the installation:

```bash
# Check if the ClusterIssuer is ready
kubectl get clusterissuer -o wide

# Check cert-manager pods
kubectl get pods -n tls-system
```

## Troubleshooting

If you encounter issues:

1. Check cert-manager logs:
   ```bash
   kubectl logs -n tls-system -l app=cert-manager
   ```

2. Check the certificate status:
   ```bash
   kubectl describe certificate -n your-namespace
   ```

3. Check the challenge status:
   ```bash
   kubectl get challenges -n your-namespace
   ```