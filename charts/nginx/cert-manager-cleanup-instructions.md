# Cert-Manager Namespace Cleanup Instructions

This document explains how to use the provided script to delete everything from the cert-manager namespace in your AKS cluster.

## Usage Instructions

1. Make the script executable:
```bash
chmod +x delete-cert-manager.sh
```

2. Run the script:
```bash
./delete-cert-manager.sh
```

## What the Script Does

The script performs the following operations:
- Uninstalls the cert-manager Helm release
- Deletes all resources in the cert-manager namespace
- Removes cert-manager CRDs (Custom Resource Definitions)
- Deletes cert-manager ClusterRoles and ClusterRoleBindings
- Finally, deletes the cert-manager namespace itself

## Troubleshooting

If you encounter permission issues, ensure you're using an account with sufficient privileges in your AKS cluster.