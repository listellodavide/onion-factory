helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
kubectl create namespace ingress-basic

```bash
helm install ingress-nginx ingress-nginx/ingress-nginx \
    --namespace ingress-basic \
    --set controller.replicaCount=1 \
    --set controller.nodeSelector."kubernetes\.io/os"=linux \
    --set defaultBackend.nodeSelector."kubernetes\.io/os"=linux \
    --set controller.service.externalTrafficPolicy=Local

```
uninstall
`helm uninstall ingress-nginx -n ingress-basic`


## Certificate

0. (one time):
helm repo add jetstack https://charts.jetstack.io
helm repo update

1. Create cert-manager namespace if it doesn't exist
```bash
kubectl create namespace cert-manager
```

2. Label the certificate manager namespace
```bash
kubectl label namespace cert-manager cert-manager.io/disable-validation=true
```

3. Install cert-manager (Option 1 - recommended)
```bash
# Install cert-manager with Helm and let it manage CRDs
helm install cert-manager jetstack/cert-manager \
      --namespace cert-manager \
      --version v1.18.2 \
      --set crds.enabled=true \
      --set startupapicheck.enabled=false
```

Note: The `startupapicheck.enabled=false` flag disables the startup API check job that often fails with a BackoffLimitExceeded error in development environments.

Important: Starting with cert-manager v1.18.0, the default private key rotation policy for Certificate resources has changed to `Always`. This means that private keys will be automatically rotated when certificates are renewed. For more information, refer to the [1.18 release notes](https://cert-manager.io/docs/releases/release-notes/release-notes-1.18).


kubectl apply -f charts/nginx/cluster-issuer.yaml -n ingress-basic