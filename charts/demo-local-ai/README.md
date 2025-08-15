# Demo Local AI Helm Chart

This Helm chart deploys the Demo Local AI application and its dependencies in a Kubernetes cluster.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (if persistence is enabled)

## Installing the Chart

To install the chart with the release name `demo-local-ai`:

```bash
helm install demo-local-ai ./charts/demo-local-ai -f charts/demo-local-ai/values-aks-dev.yaml
```

The command deploys Demo Local AI on the Kubernetes cluster with default configuration. The [Parameters](#parameters) section lists the parameters that can be configured during installation.

## Uninstalling the Chart

To uninstall/delete the `demo-local-ai` deployment:

```bash
helm uninstall demo-local-ai
```

## Parameters

### Global Parameters

| Name                   | Description                                     | Value           |
|------------------------|-------------------------------------------------|-----------------|
| `namespace.create`     | Create the namespace                            | `true`          |
| `namespace.name`       | Name of the namespace                           | `demo-local-ai` |

### Application Parameters

| Name                                    | Description                                        | Value                     |
|-----------------------------------------|----------------------------------------------------|---------------------------|
| `application.name`                      | Name of the application                            | `demo-local-ai`           |
| `application.replicas`                  | Number of replicas                                 | `1`                       |
| `application.image.repository`          | Application image repository                       | `moldovean/onion-factory` |
| `application.image.tag`                 | Application image tag                              | `v0.0.5`                  |
| `application.image.pullPolicy`          | Application image pull policy                      | `IfNotPresent`            |
| `application.service.type`              | Service type                                       | `LoadBalancer`            |
| `application.service.port`              | Service port                                       | `8080`                    |
| `application.service.targetPort`        | Service target port                                | `8080`                    |
| `application.probes.readiness.path`     | Path for readiness probe                           | `/api-docs`               |
| `application.probes.liveness.path`      | Path for liveness probe                            | `/api-docs`               |

### PostgreSQL Parameters

| Name                                | Description                                  | Value              |
|-------------------------------------|----------------------------------------------|-------------------|
| `postgres.enabled`                  | Enable PostgreSQL                            | `true`            |
| `postgres.image.repository`         | PostgreSQL image repository                  | `ankane/pgvector` |
| `postgres.image.tag`                | PostgreSQL image tag                         | `latest`          |
| `postgres.persistence.enabled`      | Enable persistence for PostgreSQL            | `true`            |
| `postgres.persistence.size`         | Size of the persistent volume                | `1Gi`             |
| `postgres.env.POSTGRES_USER`        | PostgreSQL username                          | `postgres`        |
| `postgres.env.POSTGRES_DB`          | PostgreSQL database name                     | `vectordb`        |

### Ingress Parameters

| Name                  | Description                                  | Value                 |
|-----------------------|----------------------------------------------|----------------------|
| `ingress.enabled`     | Enable ingress                               | `true`               |
| `ingress.className`   | Ingress class name                           | `nginx`              |
| `ingress.host`        | Hostname for the ingress                     | `demo-local-ai.local`|
| `ingress.clusterIssuer`| Cert-manager cluster issuer for TLS certificates | `letsencrypt-dev`  |

### Configuration Parameters

| Name                                  | Description                                  | Value                     |
|---------------------------------------|----------------------------------------------|---------------------------|
| `config.springProfilesActive`         | Spring active profile                        | `docker`                  |
| `config.springAiOpenaiBaseUrl`        | OpenAI base URL                              | `http://airunner:12432/engines` |
| `config.stripeApiPublicKey`           | Stripe API public key                        | `pk_test_...`             |

### Secret Parameters

| Name                            | Description                                  | Value        |
|---------------------------------|----------------------------------------------|-------------|
| `secrets.springR2dbcPassword`   | PostgreSQL password for R2DBC                | `postgres`  |
| `secrets.springDatasourcePassword` | PostgreSQL password for datasource        | `postgres`  |
| `secrets.stripeApiSecretKey`    | Stripe API secret key                        | `changeit`  |

## Configuration

### Host Configuration

For the application to be accessible via http://demo-local-ai.local:8080/, you need to add an entry to your hosts file:

```bash
# On Linux/macOS (including Mac M1)
echo "127.0.0.1 demo-local-ai.local" | sudo tee -a /etc/hosts

# On Windows
# Add the following line to C:\Windows\System32\drivers\etc\hosts:
# 127.0.0.1 demo-local-ai.local
```

#### Mac M1 Specific Configuration

On Mac M1 machines, you may need to ensure:
1. You have an ingress controller installed in your Kubernetes cluster
2. The hosts file is properly configured as shown above
3. Your Kubernetes service is properly exposing port 8080

### Using a custom values file

To customize the deployment, create a `values.yaml` file with your changes and use it when installing the chart:

```bash
helm install demo-local-ai ./charts/demo-local-ai -f values.yaml
```

### Example values file

```yaml
application:
  replicas: 2
  image:
    tag: v0.0.6

postgres:
  persistence:
    size: 5Gi

secrets:
  springR2dbcPassword: mySecurePassword
  springDatasourcePassword: mySecurePassword
  stripeApiSecretKey: sk_test_myStripeKey
```

## Troubleshooting

### Mac M1 Specific Issues

If you're using a Mac M1 and cannot access http://demo-local-ai.local:8080/:

1. Verify your hosts file has been updated correctly:
   ```bash
   cat /etc/hosts | grep demo-local-ai.local
   ```

2. Ensure your Kubernetes ingress controller is running:
   ```bash
   kubectl get pods -n ingress-nginx
   ```
   
   If not installed, install the NGINX Ingress Controller:
   ```bash
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
   ```

3. Check if the application pods are running:
   ```bash
   kubectl get pods -n demo-local-ai
   ```

4. Verify the ingress resource is properly configured:
   ```bash
   kubectl get ingress -n demo-local-ai
   ```

5. If using Docker Desktop, ensure that port 8080 is not being used by another application.

## TLS Certificate Management with Let's Encrypt

This chart supports automatic TLS certificate management using cert-manager and Let's Encrypt. To use this feature:

1. Ensure cert-manager is installed in your cluster:
   ```bash
   # Add the Jetstack Helm repository
   helm repo add jetstack https://charts.jetstack.io
   helm repo update

   # Install cert-manager with CRDs
   helm install cert-manager jetstack/cert-manager \
       --namespace cert-manager \
       --create-namespace \
       --version v1.18.2 \
       --set crds.enabled=true \
       --set startupapicheck.enabled=false
   ```

2. Create a ClusterIssuer for Let's Encrypt:
   ```bash
   # Apply the ClusterIssuer configuration
   kubectl apply -f charts/nginx/cluster-issuer.yaml
   ```

3. Configure your ingress in values.yaml:
   ```yaml
   ingress:
     enabled: true
     className: nginx
     host: your-domain.com
     clusterIssuer: letsencrypt-dev  # Must match the name in your ClusterIssuer
   ```

4. Install or upgrade your chart:
   ```bash
   helm upgrade --install demo-local-ai ./charts/demo-local-ai \
     -f ./charts/demo-local-ai/values-aks-dev.yaml \
     --namespace demo-local-ai \
     --create-namespace
     
   ```
   
   > **Note:** The chart is designed to handle both cases where the namespace exists or doesn't exist. The `--create-namespace` flag will create the namespace if it doesn't exist, and the chart will skip namespace creation if it already exists.

The chart will automatically:
- Create the namespace with proper Helm labels and annotations
- Configure the ingress with TLS and the specified Let's Encrypt ClusterIssuer
- Request and manage certificates for your domain

You can check the status of your certificate:
```bash
kubectl get certificate -n demo-local-ai
```