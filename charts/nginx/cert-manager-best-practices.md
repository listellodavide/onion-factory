# Cert-Manager Best Practices for Kubernetes Ingress

## Summary of Changes

To fix the issue with cert-manager HTTP01 challenges and pathType incompatibility, the following changes were made:


2. Added the same annotations to `charts/demo-local-ai/templates/ingress.yaml`, using template variables:
   ```yaml
   acme.cert-manager.io/http01-ingress-class: {{ .Values.ingress.className }}
   acme.cert-manager.io/http01-edit-in-place: "true"
   cert-manager.io/issue-temporary-certificate: "true"
   acme.cert-manager.io/http01-solver-ingress-class: {{ .Values.ingress.className }}
   ```

3. Created documentation explaining the issue, solution, and verification steps.

## Best Practices for Cert-Manager with Ingress-Nginx

1. **Use appropriate annotations for HTTP01 challenges**:
   - Always include `acme.cert-manager.io/http01-edit-in-place: "true"` to avoid pathType issues
   - Specify the ingress class with `acme.cert-manager.io/http01-ingress-class` and `acme.cert-manager.io/http01-solver-ingress-class`

2. **Avoid using deprecated annotations**:
   - Use `spec.ingressClassName` instead of the deprecated `kubernetes.io/ingress.class` annotation

3. **Use consistent secret names**:
   - Use different secret names for ACME account keys and TLS certificates
   - Follow a naming convention like `<app-name>-tls-cert` for certificate secrets

4. **Monitor certificate status**:
   - Regularly check certificate status with `kubectl get certificate`
   - Set up alerts for certificate expiration

5. **Use staging environment for testing**:
   - Use Let's Encrypt staging environment during development to avoid rate limits
   - Switch to production only when everything is working correctly

6. **Handle certificate renewal properly**:
   - Ensure cert-manager has permissions to create and update resources
   - Set appropriate renewal windows (default is 30 days before expiration)

7. **Troubleshooting**:
   - Check cert-manager logs for detailed error information
   - Examine challenge resources to understand why certificate issuance might be failing
   - Use `kubectl describe` on certificates, challenges, and orders for detailed status

## Recommended Configuration for Production

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
    kubernetes.io/tls-acme: "true"
    acme.cert-manager.io/http01-edit-in-place: "true"
    acme.cert-manager.io/http01-ingress-class: nginx
    acme.cert-manager.io/http01-solver-ingress-class: nginx
    cert-manager.io/issue-temporary-certificate: "true"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - example.com
    secretName: example-tls-cert
  rules:
  - host: example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: example-service
            port:
              number: 80
```

By following these best practices, you can ensure that cert-manager works correctly with your ingress resources and successfully obtains and renews Let's Encrypt certificates.