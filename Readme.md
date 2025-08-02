1. You need Java and Docker installed on your machine.
2. You also need to have the ai/smollm2 model downloaded and available in your Docker environment.
3. Clone the repository.
4. Run the following command to build the project:
   ```bash
   ./gradlew build
   ```
   
## Troubleshooting
If you encounter issues, ensure that:
1. stop and remove all containers and volumes:
`docker compose down -v`
