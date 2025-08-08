# Demo Local AI

## Prerequisites
1. You need Java and Docker installed on your machine.
2. You also need to have the ai/smollm2 model downloaded and available in your Docker environment.
3. You need a test: STRIPE_API_SECRETKEY

## Getting Started
1. Clone the repository.
2. Run the following command to build the project:
   ```bash
   ./gradlew build
   ```
3. Start the application:
   ```bash
   ./gradlew bootRun
   ```

## Integration Testing


This task executes the greet-client-java.sh script to test the greet endpoint. Make sure the server is running before executing this task.


## API Documentation

The application includes Swagger/OpenAPI documentation for all REST endpoints. Once the application is running, you can access:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)



## integration with Stripe
Add in your environment variables:
STRIPE_API_SECRETKEY
`stripe listen --forward-to localhost:8080/api/payments/webhook`

## Build an image
`./gradlew  bootBuildImage --no-publishImage --imageName=onion-factory:latest`
You can use your username/onion-factory:latest and skip the --no-publishImage to upload the image to your docker hub.
But don't be an idiot and upload the image with your secret key in it.
then try: 

`docker compose -f docker-compose.dev.yaml up`
to close:

`docker compose -f docker-compose.dev.yaml down -v`

## CI/CD
git tag v1.0.0
git push origin v1.0.0

## K8s
read the k8s/readme.md

## Helm Chart

The application can also be deployed using Helm charts:

1. Make sure you have Helm installed (version 3.2.0+)
2. Configure your hosts file to resolve the application hostname:
   ```bash
   # On Linux/macOS (including Mac M1)
   echo "127.0.0.1 demo-local-ai.local" | sudo tee -a /etc/hosts
   
   # On Windows
   # Add the following line to C:\Windows\System32\drivers\etc\hosts:
   # 127.0.0.1 demo-local-ai.local
   ```
3. Deploy the application using Helm:
   ```bash
   helm install demo-local-ai ./charts/demo-local-ai
   
   ```
   to uninstall:
   
```bash
    helm uninstall demo-local-ai 
```
4. Access the application at http://demo-local-ai.local:8080/
5. For customization options and Mac M1-specific configuration, see the [Helm chart documentation](./charts/demo-local-ai/README.md)

## AKS
1. Create your AKS cluster
2. add it along the local cluster 
`az aks get-credentials --resource-group rg-ak8s-dev --name my-ak8s-dev --overwrite-existing`
3. check with `kubectl config get-contexts`

## Troubleshooting
If you encounter issues, ensure that:
1. Stop and remove all containers and volumes:
   ```bash
   docker compose down -v
   ```
2. Or from volumes in Docker Desktop delete the volumes

### Mac M1 Specific Issues
If you're using a Mac M1 and having trouble accessing the application after Helm installation:
1. Verify your hosts file has been properly configured
2. Check that the ingress controller is running
3. See the [Helm chart troubleshooting guide](./charts/demo-local-ai/README.md#mac-m1-specific-issues) for detailed steps


#### TEMP
#          ./gradlew bootBuildImage --imageName=${{ secrets.DOCKERHUB_USERNAME }}/onion-factory:${GITHUB_REF#refs/tags/}