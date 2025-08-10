# Certificate Issue Fix

## Problem
The certificate was not being issued correctly because the same secret name (`letsencrypt-dev-account-key`) was being used for two different purposes:
1. As the ACME account private key in the ClusterIssuer
2. As the TLS certificate secret in the Ingress resources

This naming conflict prevented cert-manager from properly creating and managing the certificate.

## Solution
The solution was to use different secret names for these two different purposes:
- Keep `letsencrypt-dev-account-key` as the ACME account private key in the ClusterIssuer
- Use `demo-local-ai-tls-cert` as the TLS certificate secret in the Ingress resources

Changes made:
1. Updated `/charts/nginx/myingress.yaml` to use `demo-local-ai-tls-cert` as the secretName
2. Updated `/charts/demo-local-ai/templates/ingress.yaml` to use `demo-local-ai-tls-cert` as the secretName
3. Enhanced the verification script to apply both changes and provide better diagnostics

## Verification
To verify the fix:
1. Run the verification script:
   ```bash
   ./charts/nginx/verify-certificate.sh
   ```

2. Check that the certificate is in the "Ready" state:
   ```bash
   kubectl get certificate -n demo-local-ai
   ```

3. Once the certificate is ready, you should be able to access your application via HTTPS:
   ```bash
   curl -k https://helloworlds.space
   ```

## Troubleshooting
If the certificate is still not ready after applying the fix:
- Wait longer for Let's Encrypt to issue the certificate (can take several minutes)
- Check the cert-manager logs for any errors:
  ```bash
  kubectl logs -n cert-manager -l app=cert-manager
  ```
- Verify that the HTTP-01 challenges are being created and solved:
  ```bash
  kubectl get challenges -n demo-local-ai
  ```