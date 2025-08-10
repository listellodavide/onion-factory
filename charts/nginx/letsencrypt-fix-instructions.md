# Let's Encrypt Certificate Fix

## Issue
The Kubernetes cluster was using a fake certificate instead of a proper Let's Encrypt certificate:
```
Common Name (CN): Kubernetes Ingress Controller Fake Certificate
Organization (O): Acme Co
```

## Root Cause
The ingress configurations were missing the required annotation `kubernetes.io/tls-acme: "true"` which is needed for proper Let's Encrypt integration with cert-manager.

## Changes Made
1. Added the missing annotation to the Helm chart's ingress template:
   ```yaml
   annotations:
     cert-manager.io/cluster-issuer: {{ .Values.ingress.clusterIssuer }}
     kubernetes.io/tls-acme: "true"
   ```

2. Added the missing annotation to the direct ingress configuration:
   ```yaml
   annotations:
     cert-manager.io/cluster-issuer: letsencrypt-dev
     kubernetes.io/tls-acme: "true"
     kubectl.kubernetes.io/last-applied-configuration: ""
   ```

## Verification Steps
1. Make the script executable and run it:
   ```bash
   chmod +x charts/nginx/verify-letsencrypt.sh
   ./charts/nginx/verify-letsencrypt.sh
   ```

2. If the certificate is still showing as "Kubernetes Ingress Controller Fake Certificate", you may need to delete the existing certificate secret and let cert-manager recreate it:
   ```bash
   kubectl delete secret demo-local-ai-tls-cert -n demo-local-ai
   ```

3. After deleting the secret, wait a few minutes and check the certificate status again:
   ```bash
   kubectl get certificate -n demo-local-ai
   kubectl describe certificate demo-local-ai-tls-cert -n demo-local-ai
   ```

4. To verify the certificate in your browser, visit https://helloworlds.space and check the certificate details.

## Additional Troubleshooting
If you're still experiencing issues:

1. Check if cert-manager is running properly:
   ```bash
   kubectl get pods -n cert-manager
   ```

2. Check the cert-manager logs for any errors:
   ```bash
   kubectl logs -n cert-manager -l app=cert-manager
   ```

3. Verify the ClusterIssuer is correctly configured:
   ```bash
   kubectl get clusterissuer letsencrypt-dev -o wide
   kubectl describe clusterissuer letsencrypt-dev
   ```

4. Check if there are any challenges in progress:
   ```bash
   kubectl get challenges -n demo-local-ai
   ```

5. Make sure your domain's DNS is properly configured to point to the external IP of your ingress controller.