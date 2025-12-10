# SMS Checker / Frontend

The frontend allows users to interact with the model in the backend through a web-based UI.

The frontend is implemented with Spring Boot. Any classification requests will be delegated to the `backend` service that serves the model in which more information is provided in **model-service**.

In order to access the backend, from the **workspace/app**, you must first authenticate with the GitHub package registry. Copy [./maven-settings.xml.example](./maven-settings.xml.example) to [./maven-settings.xml](./maven-settings.xml) and replace `GITHUB_USERNAME` by your GitHub username and `GITHUB_TOKEN` by a [Personal Access Token (classic)](https://github.com/settings/tokens) with scope `read:packages`. You can then run the following command:

```
docker compose -f ./docker-compose.dev.yaml up --build
```

The server runs on port 8080. Once the startup has finished, you can access [localhost:8080](http://localhost:8080) in your browser which opens a page with a simple "Hello World!". By accessing [localhost:8080/sms](http://localhost:8080/sms), it opens the application.

## Prometheus Metrics

The application exposes custom Prometheus metrics at `/metrics`.:

- `sms_classification_requests_total{service="frontend"}` - Counter tracking total SMS classification requests
- `sms_classification_results_total{result="spam"}` - Counter for spam classifications
- `sms_classification_results_total{result="ham"}` - Counter for ham classifications
- `sms_current_message_length_chars{service="frontend",type="current"}` - Gauge showing the current message length being processed
- `sms_classification_duration_milliseconds{service="frontend",operation="classify"}` - Histogram tracking response time distribution
- `sms_message_length_chars{service="frontend",metric_type="histogram"}` - Histogram of message lengths distribution

All metrics include labels for breakdown and filtering. Also, they are manually implemented using custom Prometheus format classes and are automatically discovered by Prometheus via ServiceMonitor in Kubernetes.

Example:
```
curl http://localhost:8080/metrics
```
