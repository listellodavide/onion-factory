# Certificate Conflict Resolution

## Problem
Two certificates were showing as "False" (not ready) in the demo-local-ai namespace:
```
kubectl get certificate -n demo-local-ai
NAME                        READY   SECRET                   AGE
demo-local-ai-certificate   False   demo-local-ai-tls-cert   2m1s
demo-local-ai-tls-cert      False   demo-local-ai-tls-cert   8m9s
```

The issue was caused by:
1. Both certificates were trying to use the same secret name (`demo-local-ai-tls-cert`)
2. One certificate was auto-created by the Ingress controller due to annotations
3. The other was our custom Certificate resource with proper subject fields
4. The certificates were conflicting with each other, resulting in "IncorrectIssuer" errors

## Solution
The solution involved several steps:

1. **Remove auto-creation annotations from the Ingress template**:
   - Removed `kubernetes.io/tls-acme: "true"` annotation
   - Removed `cert-manager.io/cluster-issuer: {{ .Values.ingress.clusterIssuer }}` annotation
   - These annotations were causing cert-manager to automatically create a certificate

2. **Update the custom Certificate resource**:
   - Renamed the certificate to match the name expected by the Ingress (`demo-local-ai-tls-cert`)
   - Updated the commonName and dnsNames to use only the valid domain "helloworlds.space"
   - Removed "cert-manager.local" as it's not a valid public domain (Let's Encrypt can't issue for .local domains)

3. **Clean up existing resources**:
   - Deleted both the certificate and its secret to start completely fresh
   - Applied the updated Certificate resource

## Implementation Details
1. Modified `/charts/demo-local-ai/templates/ingress.yaml` to remove auto-creation annotations
2. Updated `/charts/nginx/certificate.yaml` to:
   - Use the correct name (`demo-local-ai-tls-cert`)
   - Use "helloworlds.space" as both commonName and dnsName
   - Keep the proper subject fields (Organization and Organizational Unit)

## Verification
To verify the fix:
1. Check that only one certificate exists and is in the "Issuing" state:
   ```bash
   kubectl get certificate -n demo-local-ai
   ```

2. Verify that the certificate request is approved:
   ```bash
   kubectl get certificaterequest -n demo-local-ai
   ```

3. Once the certificate is ready (which may take several minutes for Let's Encrypt to complete the ACME challenge), you should be able to access your application via HTTPS.

## Notes
- Certificate issuance through Let's Encrypt can take several minutes to complete
- The ACME HTTP-01 challenge requires that your domain is publicly accessible
- Let's Encrypt cannot issue certificates for non-public domains (like .local domains)
- When using cert-manager with Ingress resources, it's best to either:
  1. Let cert-manager auto-create certificates from Ingress annotations, OR
  2. Create custom Certificate resources and reference them in the Ingress
  3. Don't do both at the same time to avoid conflicts