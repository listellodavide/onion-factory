# Cert-Manager Installation Guide

## Happy Flow Installation

Follow these steps for a smooth installation of cert-manager:

1. Create cert-manager namespace:
```bash
kubectl create namespace cert-manager
```

2. Label the certificate manager namespace:
```bash
kubectl label namespace cert-manager cert-manager.io/disable-validation=true
```

3. Install cert-manager with Helm:
```bash
helm install cert-manager jetstack/cert-manager \
      --namespace cert-manager \
      --version v1.18.2 \
      --set crds.enabled=true \
      --set startupapicheck.enabled=false
```

Note: The `startupapicheck.enabled=false` flag disables the startup API check job that often fails in development environments.

Important: Starting with cert-manager v1.18.0, the default private key rotation policy for Certificate resources has changed to `Always`. This means that private keys will be automatically rotated when certificates are renewed. For more information, refer to the [1.18 release notes](https://cert-manager.io/docs/releases/release-notes/release-notes-1.18).

## Troubleshooting Common Issues

### Startup API Check Failure

**Problem**: The startupapicheck job fails with BackoffLimitExceeded error.

**Solution**: Disable the startupapicheck job during installation:
```bash
--set startupapicheck.enabled=false
```

### CRD Conflicts

**Problem**: When installing cert-manager, you get errors about existing CustomResourceDefinitions.

**Solution**:
1. Delete the existing CRDs that are causing conflicts
2. Then install cert-manager with Helm

### ClusterRole Conflicts

**Problem**: When installing cert-manager in a different namespace than a previous installation, you get ClusterRole conflict errors.

**Solution**:
1. Check if cert-manager is installed in another namespace
2. Uninstall the existing cert-manager installation:
   ```bash
   helm uninstall cert-manager -n cert-manager
   ```
3. Delete any remaining ClusterRoles and ClusterRoleBindings
4. Then install cert-manager in the desired namespace

### Name Already In Use

**Problem**: When attempting to install cert-manager, you get "name already in use" error.

**Solution**:
1. Uninstall the existing cert-manager release:
   ```bash
   helm uninstall cert-manager -n ingress-basic
   ```
2. Verify that the release has been removed:
   ```bash
   helm list -n ingress-basic
   ```
3. Then install cert-manager again

## Best Practices

1. Use `--set startupapicheck.enabled=false` in development environments
2. Always use a single method to manage CRDs (either Helm or kubectl)
3. When uninstalling cert-manager, clean up CRDs if they won't be reused
4. Use consistent namespaces for related components when possible
5. Always use Helm to uninstall Helm-installed components
6. For a complete cleanup before reinstallation, remove all related resources

## Cross-Namespace Functionality

Cert-manager can be installed in a different namespace (e.g., "cert-manager") than your ingress controller (e.g., "ingress-basic") and still function correctly because:

1. ClusterIssuer resources are cluster-wide and not namespace-specific
2. Cert-manager controllers watch for resources across all namespaces by default
3. The ingress controller can communicate with cert-manager regardless of namespace

If you're using an ingress controller in the "ingress-basic" namespace, you don't need to install cert-manager in the same namespace. The default "cert-manager" namespace is recommended for better organization and separation of concerns.