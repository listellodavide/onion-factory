# Delete all old certificates, issuers, and challenges
kubectl delete certificate --all -n demo-local-ai
kubectl delete clusterissuer --all
kubectl delete challenge --all -n demo-local-ai
kubectl delete certificaterequest --all -n demo-local-ai

2
helm repo add jetstack https://charts.jetstack.io
helm repo update

# Install cert-manager with CRDs
helm install cert-manager jetstack/cert-manager \
--namespace cert-manager \
--create-namespace \
--version v1.14.0 \
--set installCRDs=true

3
# File: letsencrypt-staging.yaml
```yaml
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-staging
spec:
  acme:
    # staging server (untrusted certs — for testing only)
    server: https://acme-staging-v02.api.letsencrypt.org/directory
    email: net.vrabie@gmail.com
    privateKeySecretRef:
      name: letsencrypt-staging
    solvers:
      - http01:
          ingress:
            class: nginx
```

kubectl apply -f charts/temp/letsencrypt-staging.yaml

4
# File: helloworlds-space-tls.yaml
```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: helloworlds-space-tls
  namespace: demo-local-ai
spec:
  secretName: helloworlds-space-tls
  dnsNames:
    - helloworlds.space
  issuerRef:
    name: letsencrypt-staging
    kind: ClusterIssuer
```

kubectl apply -f helloworlds-space-tls.yaml -n demo-local-ai
kubectl get certificate helloworlds-space-tls -n demo-local-ai -w

5 Configure the ingress
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: demo-local-ai-ingress
  namespace: demo-local-ai
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-staging
spec:
  tls:
    - hosts:
        - helloworlds.space
      secretName: helloworlds-space-tls
  rules:
    - host: helloworlds.space
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: demo-local-ai
              port:
                number: 8080

```
kubectl apply -f demo-local-ai-ingress.yaml

6 Verification
kubectl describe ingress demo-local-ai-ingress -n demo-local-ai
curl -vk https://helloworlds.space


# Then move to Prod

```yaml
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: your-email@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx

```

Update the certificate
```yaml
spec:
  issuerRef:
    name: letsencrypt-prod
    kind: ClusterIssuer

```

kubectl apply -f helloworlds-space-tls.yaml -n demo-local-ai