#!/bin/bash
# Script to delete all resources from the cert-manager namespace and the namespace itself

echo "Starting cleanup of cert-manager namespace..."

# 1. Delete the cert-manager Helm release first (this is the proper way to uninstall Helm charts)
echo "Uninstalling cert-manager Helm release..."
helm uninstall cert-manager -n cert-manager

# 2. Delete any remaining resources in the cert-manager namespace
echo "Deleting remaining resources in cert-manager namespace..."

# Delete all resources by type
RESOURCE_TYPES="deployment service pod job cronjob statefulset daemonset configmap secret serviceaccount ingress persistentvolumeclaim"

for resource_type in $RESOURCE_TYPES; do
  echo "Deleting all $resource_type resources..."
  kubectl delete $resource_type --all -n cert-manager
done

# 3. Delete any cluster-wide resources created by cert-manager
echo "Deleting cluster-wide resources created by cert-manager..."

# Delete CRDs (Custom Resource Definitions)
echo "Deleting cert-manager CRDs..."
kubectl delete crd certificaterequests.cert-manager.io
kubectl delete crd certificates.cert-manager.io
kubectl delete crd challenges.acme.cert-manager.io
kubectl delete crd clusterissuers.cert-manager.io
kubectl delete crd issuers.cert-manager.io
kubectl delete crd orders.acme.cert-manager.io

# Delete ClusterRoles and ClusterRoleBindings
echo "Deleting cert-manager ClusterRoles and ClusterRoleBindings..."
kubectl delete clusterrole cert-manager-cainjector
kubectl delete clusterrole cert-manager-controller-approve:cert-manager-io
kubectl delete clusterrole cert-manager-controller-certificates
kubectl delete clusterrole cert-manager-controller-challenges
kubectl delete clusterrole cert-manager-controller-clusterissuers
kubectl delete clusterrole cert-manager-controller-ingress-shim
kubectl delete clusterrole cert-manager-controller-issuers
kubectl delete clusterrole cert-manager-controller-orders
kubectl delete clusterrole cert-manager-edit
kubectl delete clusterrole cert-manager-view
kubectl delete clusterrolebinding cert-manager-cainjector
kubectl delete clusterrolebinding cert-manager-controller-approve:cert-manager-io
kubectl delete clusterrolebinding cert-manager-controller-certificates
kubectl delete clusterrolebinding cert-manager-controller-challenges
kubectl delete clusterrolebinding cert-manager-controller-clusterissuers
kubectl delete clusterrolebinding cert-manager-controller-ingress-shim
kubectl delete clusterrolebinding cert-manager-controller-issuers
kubectl delete clusterrolebinding cert-manager-controller-orders

# 4. Finally, delete the namespace itself
echo "Deleting cert-manager namespace..."
kubectl delete namespace cert-manager

echo "Cleanup complete!"