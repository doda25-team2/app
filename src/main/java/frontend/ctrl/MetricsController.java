package frontend.ctrl;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import frontend.metrics.AppMetrics;

@Controller
public class MetricsController {

    private final AppMetrics metrics;

    public MetricsController(AppMetrics metricsInstance) {
        this.metrics = metricsInstance;
    }

    @GetMapping(value = "/metrics", produces = "text/plain; version=0.0.4; charset=utf-8")
    public ResponseEntity<String> metrics() {
        // Prometheus expects versioned text/plain responses with a trailing newline.
        String body = metrics.exportMetrics();
        if (!body.endsWith("\n")) {
            body = body + "\n";
        }

        MediaType contentType = MediaType.parseMediaType("text/plain; version=0.0.4; charset=utf-8");

        return ResponseEntity
            .ok()
            .contentType(contentType)
            .cacheControl(CacheControl.noStore())
            .body(body);
    }
}
