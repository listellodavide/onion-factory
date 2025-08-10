# HTTP01 Challenge PathType Fix

## Problem

When using cert-manager with HTTP01 challenges, the following error was occurring:

```
admission webhook "validate.nginx.ingress.kubernetes.io" denied the request: ingress contains invalid paths: path /.well-known/acme-challenge/s8W3yjW0Ou0VGsgKl8rhyKOEhlAj_VEJXx9Uh1XXXQ4 cannot be used with pathType Exact
```

This error occurs because cert-manager was creating temporary ingress resources with `pathType: Exact` for ACME HTTP01 challenges, but the challenge paths contain characters that are not compatible with the Exact path type in nginx ingress controller.

## Solution

The solution is to add specific annotations to the ingress resources to control how cert-manager creates and manages HTTP01 challenges:

1. `acme.cert-manager.io/http01-ingress-class`: Specifies which ingress class to use for HTTP01 challenges
2. `acme.cert-manager.io/http01-edit-in-place`: When set to "true", cert-manager will modify the existing ingress resource instead of creating a new one
3. `cert-manager.io/issue-temporary-certificate`: When set to "true", cert-manager will issue a temporary certificate while waiting for the real one
4. `acme.cert-manager.io/http01-solver-ingress-class`: Specifies which ingress class to use for the solver

These annotations help cert-manager properly configure the HTTP01 challenges without using incompatible path types.

## Verification

To verify that the fix has been applied and the certificate is being issued correctly:

1. Check the status of the certificate:
   ```bash
   kubectl get certificate -n demo-local-ai
   ```
   The certificate should show as "Ready: True" once it's been issued.

2. Check the challenges to ensure they're being completed successfully:
   ```bash
   kubectl get challenges -n demo-local-ai
   ```
   Challenges should show as "State: valid" once they've been completed.

3. Check the cert-manager logs to ensure there are no errors:
   ```bash
   kubectl logs -n cert-manager -l app=cert-manager
   ```
   The logs should no longer show the pathType error.

4. Verify the certificate is valid by accessing your site via HTTPS:
   ```bash
   curl -v https://helloworlds.space
   ```
   The certificate should be issued by Let's Encrypt, not the "Kubernetes Ingress Controller Fake Certificate".

## Additional Information

If you're still experiencing issues, you can try the following:

1. Delete the existing certificate and let cert-manager recreate it:
   ```bash
   kubectl delete certificate -n demo-local-ai demo-local-ai-tls-cert
   ```

2. Check if there are any existing challenges that might be interfering:
   ```bash
   kubectl get challenges -n demo-local-ai
   ```
   If there are failed challenges, you can delete them:
   ```bash
   kubectl delete challenges -n demo-local-ai --all
   ```

3. Restart the cert-manager pod to ensure it picks up the new configuration:
   ```bash
   kubectl rollout restart deployment -n cert-manager cert-manager
   ```

These annotations should resolve the issue with HTTP01 challenges and allow cert-manager to successfully obtain and renew Let's Encrypt certificates.