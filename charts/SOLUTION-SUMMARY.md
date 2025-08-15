# TLS Setup for AKS - Solution Summary

## Requirements

The requirements for this solution were:

1. Divide the TLS setup into two separate Helm charts:
   - First chart: Install nginx-ingress including the load balancer
   - Manual step: Configure in Azure the public IP to the DNS zone
   - Second chart: Proceed with the certification
2. Enable HTTPS access to the demo-local-ai application from execodex.com

## Solution Overview

The solution has been implemented as two separate Helm charts:

### 1. nginx-ingress-aks

This chart installs the NGINX Ingress Controller on Azure Kubernetes Service (AKS). It:
- Creates the necessary namespace (ingress-basic)
- Installs the ingress-nginx controller with appropriate settings for AKS
- Provides a load balancer with an external IP address

After installing this chart, you need to:
1. Get the external IP address of the load balancer
2. Configure your DNS to point your domain (execodex.com) to this IP address
3. Wait for DNS propagation to complete

### 2. cert-manager-aks

This chart installs cert-manager and configures TLS certificates for your application. It:
- Creates the necessary namespaces (cert-manager, demo-local-ai)
- Installs cert-manager with appropriate settings
- Creates a ClusterIssuer for Let's Encrypt
- Creates a Certificate resource for your domain
- Creates an Ingress resource for the demo-local-ai application

This chart should only be installed after:
1. The nginx-ingress-aks chart has been installed
2. The DNS has been configured to point to the external IP of the ingress controller
3. DNS propagation has completed

## Installation Workflow

The complete installation workflow is:

1. Install the nginx-ingress-aks chart:
   ```bash
   helm dependency update ./charts/nginx-ingress-aks
   helm install nginx-ingress-aks ./charts/nginx-ingress-aks --namespace ingress-basic --create-namespace
   ```
   
   > **Important**: The `--create-namespace` flag is required to ensure the namespace is created before the chart is installed.

2. Get the external IP address of the ingress controller:
   ```bash
   kubectl get service -n ingress-basic nginx-ingress-aks-ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
   ```

3. Configure your DNS to point your domain (execodex.com) to this IP address in Azure DNS zone

4. Wait for DNS propagation to complete (you can verify with `nslookup execodex.com`)

5. Install the cert-manager-aks chart:
   ```bash
   helm upgrade --install cert-manager jetstack/cert-manager \
    -n cert-manager \
    --version v1.18.2 \
    --set crds.enabled=true \
    --set startupapicheck.enabled=false

   ```
   
   > **Important**: The `--create-namespace` flag is required to ensure the namespace is created before the chart is installed.
   >
   > The chart now includes a mechanism to ensure CRDs are installed and ready before creating Certificate and ClusterIssuer resources. This is done using Helm hooks and a wait job.

6. Verify the certificate issuance:
   ```bash
   kubectl get certificate -n demo-local-ai
   ```

7. Access your application at https://execodex.com

## Benefits of This Approach

This two-step approach provides several benefits:

1. **Clear Separation of Concerns**: The ingress controller and certificate management are handled separately
2. **Manual DNS Configuration**: Allows for proper verification and control of DNS settings
3. **Reduced Complexity**: Each chart focuses on a specific aspect of the setup
4. **Better Troubleshooting**: Issues with ingress or certificate issuance can be isolated and addressed separately

## Customization

Both charts are highly customizable through their respective `values.yaml` files. You can modify:
- The domain name
- The certificate settings
- The ingress configuration
- The namespace names
- And more

See the README.md files in each chart directory for more details on the available configuration options.