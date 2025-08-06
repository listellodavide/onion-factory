# Demo Local AI Helm Chart

This Helm chart deploys the Demo Local AI application and its dependencies in a Kubernetes cluster.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (if persistence is enabled)

## Installing the Chart

To install the chart with the release name `demo-local-ai`:

```bash
helm install demo-local-ai ./charts/demo-local-ai
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