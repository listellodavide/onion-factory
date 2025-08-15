# NGINX Ingress Controller for AKS

This Helm chart installs the NGINX Ingress Controller on Azure Kubernetes Service (AKS). It is the first part of a two-step process to set up TLS for your applications.

## Prerequisites

- Kubernetes cluster running on AKS
- Helm 3.x installed
- kubectl configured to communicate with your AKS cluster

## Installation

1. Update the dependencies:
```bash
helm dependency update ./charts/nginx-ingress-aks
```

2. Install the chart:
```bash
helm install nginx-ingress-aks ./charts/nginx-ingress-aks --namespace ingress-basic --create-namespace
```

> **Important**: The `--namespace ingress-basic --create-namespace` flags are required to ensure all resources are installed in the correct namespace. Without these flags, resources will be installed in the default namespace.

## Configuration

The following table lists the configurable parameters of the nginx-ingress-aks chart and their default values.

| Parameter | Description | Default |
| --------- | ----------- | ------- |
| `ingress-nginx.enabled` | Enable ingress-nginx installation | `true` |
| `ingress-nginx.controller.replicaCount` | Number of ingress controller replicas | `1` |
| `namespace.name` | Name of the namespace | `ingress-basic` |

> **Note**: The `namespace.create` parameter has been removed. The namespace is now created using the `--create-namespace` flag during installation.

## Next Steps

After installing this chart:

1. Get the external IP address of your ingress controller:
```bash
kubectl get service -n ingress-basic ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
```

2. Configure your DNS to point your domain (e.g., execodex.com) to this IP address.

3. After DNS propagation is complete, install the cert-manager-aks chart to set up TLS:
```bash
helm install cert-manager-aks ./charts/cert-manager-aks
```

## Uninstallation

To uninstall the chart:
```bash
helm uninstall nginx-ingress-aks -n ingress-basic
```