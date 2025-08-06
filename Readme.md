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
2. Deploy the application using Helm:
   ```bash
   helm install demo-local-ai ./charts/demo-local-ai
   ```
3. For customization options, see the [Helm chart documentation](./charts/demo-local-ai/README.md)

## Troubleshooting
If you encounter issues, ensure that:
1. stop and remove all containers and volumes:
`docker compose down -v`
2. Or from volumes in docker desktop delete the volumes


#### TEMP
#          ./gradlew bootBuildImage --imageName=${{ secrets.DOCKERHUB_USERNAME }}/onion-factory:${GITHUB_REF#refs/tags/}