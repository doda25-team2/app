# SMS Checker / Frontend

The frontend allows users to interact with the model in the backend through a web-based UI.

The frontend is implemented with Spring Boot. Any classification requests will be delegated to the `backend` service that serves the model in which more information is provided in **model-service**.

In order to access the backend, from the **workspace/app**, you must first authenticate with the GitHub package registry. Copy [./maven-settings.xml.example](./maven-settings.xml.example) to [./maven-settings.xml](./maven-settings.xml) and replace `GITHUB_USERNAME` by your GitHub username and `GITHUB_TOKEN` by a [Personal Access Token (classic)](https://github.com/settings/tokens) with scope `read:packages`. You can then run the following command:

```
docker compose -f ./docker-compose.dev.yaml up --build
```

The server runs on port 8080. Once the startup has finished, you can access [localhost:8080](http://localhost:8080) in your browser which opens a page with a simple "Hello World!". By accessing [localhost:8080/sms](http://localhost:8080/sms), it opens the application.
