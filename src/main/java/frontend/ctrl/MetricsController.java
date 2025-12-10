package frontend.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import frontend.metrics.AppMetrics;

@Controller
public class MetricsController {

    private final AppMetrics metrics;

    public MetricsController(AppMetrics metricsInstance) {
        this.metrics = metricsInstance;
    }

    @GetMapping("/metrics")
    @ResponseBody
    public String metrics() {
        return metrics.exportMetrics();
    }
}
