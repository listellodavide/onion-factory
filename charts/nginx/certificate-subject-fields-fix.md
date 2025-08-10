# Certificate Subject Fields Fix

## Problem
The certificate was being issued without proper subject fields:
- Common Name (CN) was set correctly
- Organization (O) was showing as `<Not Part Of Certificate>`
- Organizational Unit (OU) was showing as `<Not Part Of Certificate>`

This happens because when certificates are automatically created from Ingress resources, cert-manager doesn't set these subject fields by default.

## Solution
The solution is to create an explicit Certificate resource that specifies all the required subject fields:

1. Created a dedicated Certificate resource (`certificate.yaml`) with:
   - The same secretName as used in the Ingress (`demo-local-ai-tls-cert`)
   - Explicit subject fields for Organization and Organizational Unit
   - Proper commonName and dnsNames

2. Updated the verification script to apply this Certificate resource before applying the Ingress

## Implementation Details
The Certificate resource includes:
- Organization (O): "Demo Local AI"
- Organizational Unit (OU): "Engineering"
- Common Name (CN): "cert-manager.local"
- DNS Names: "helloworlds.space"

## Verification
To verify the fix:
1. Run the verification script:
   ```bash
   ./charts/nginx/verify-certificate.sh
   ```

2. Check that the certificate is in the "Ready" state and includes all subject fields:
   ```bash
   kubectl get certificate -n demo-local-ai
   kubectl describe certificate -n demo-local-ai
   ```

3. Once the certificate is ready, you should be able to access your application via HTTPS and see the proper subject fields in the certificate details.

## Notes
- This approach gives more control over certificate properties than relying on automatic certificate creation from Ingress resources
- The Certificate resource must use the same secretName as specified in the Ingress TLS configuration
- The Certificate and Ingress must be in the same namespace