package frontend.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all application metrics.
 * Provides methods to record events and export metrics in Prometheus format.
 */
public class AppMetrics {

    private final PrometheusCounter requestsCounter;
    private final PrometheusCounter spamCounter;
    private final PrometheusCounter hamCounter;
    private final PrometheusGauge currentMessageLengthGauge;
    private final PrometheusHistogram messageLengthHistogram;
    private final PrometheusHistogram responseDurationHistogram;

    public AppMetrics() {
        // Initialize counters
        Map<String, String> requestLabels = new HashMap<>();
        requestLabels.put("service", "frontend");
        this.requestsCounter = new PrometheusCounter(
            "sms_classification_requests_total",
            "Total number of SMS classification requests",
            requestLabels
        );

        Map<String, String> spamLabels = new HashMap<>();
        spamLabels.put("result", "spam");
        this.spamCounter = new PrometheusCounter(
            "sms_classification_results_total",
            "Total SMS classifications by result type",
            spamLabels
        );

        Map<String, String> hamLabels = new HashMap<>();
        hamLabels.put("result", "ham");
        this.hamCounter = new PrometheusCounter(
            "sms_classification_results_total",
            "Total SMS classifications by result type",
            hamLabels
        );

        // Initialize gauge
        Map<String, String> gaugeLabels = new HashMap<>();
        gaugeLabels.put("service", "frontend");
        gaugeLabels.put("type", "current");
        this.currentMessageLengthGauge = new PrometheusGauge(
            "sms_current_message_length_chars",
            "Length of currently processed message in characters",
            gaugeLabels
        );

        // Initialize histograms
        Map<String, String> histogramLabels = new HashMap<>();
        histogramLabels.put("service", "frontend");
        histogramLabels.put("metric_type", "histogram");
        this.messageLengthHistogram = new PrometheusHistogram(
            "sms_message_length_chars",
            "Distribution of SMS message lengths",
            histogramLabels,
            new double[]{10, 25, 50, 100, 200, 500}
        );

        Map<String, String> durationLabels = new HashMap<>();
        durationLabels.put("service", "frontend");
        durationLabels.put("operation", "classify");
        this.responseDurationHistogram = new PrometheusHistogram(
            "sms_classification_duration_milliseconds",
            "Time taken to classify SMS messages",
            durationLabels,
            new double[]{10, 50, 100, 250, 500, 1000, 2500}
        );
    }

    /**
     * Record a classification request with timing and result.
     */
    public void recordClassification(String result, int messageLength, long durationMs) {
        requestsCounter.increment();

        if ("spam".equalsIgnoreCase(result)) {
            spamCounter.increment();
        } else if ("ham".equalsIgnoreCase(result)) {
            hamCounter.increment();
        }

        currentMessageLengthGauge.set(messageLength);
        messageLengthHistogram.observe(messageLength);
        responseDurationHistogram.observe(durationMs);
    }

    /**
     * Export all metrics in Prometheus text exposition format.
     */
    public String exportMetrics() {
        StringBuilder output = new StringBuilder();

        // Requests counter
        output.append("# HELP ").append(requestsCounter.getName()).append(" ").append(requestsCounter.getDescription()).append("\n");
        output.append("# TYPE ").append(requestsCounter.getName()).append(" counter\n");
        output.append(requestsCounter.toPrometheusFormat()).append("\n\n");

        // Results counters (spam and ham share the same metric name but different labels)
        output.append("# HELP ").append(spamCounter.getName()).append(" ").append(spamCounter.getDescription()).append("\n");
        output.append("# TYPE ").append(spamCounter.getName()).append(" counter\n");
        output.append(spamCounter.toPrometheusFormat()).append("\n");
        output.append(hamCounter.toPrometheusFormat()).append("\n\n");

        // Current message length gauge
        output.append("# HELP ").append(currentMessageLengthGauge.getName()).append(" ").append(currentMessageLengthGauge.getDescription()).append("\n");
        output.append("# TYPE ").append(currentMessageLengthGauge.getName()).append(" gauge\n");
        output.append(currentMessageLengthGauge.toPrometheusFormat()).append("\n\n");

        // Message length histogram
        output.append("# HELP ").append(messageLengthHistogram.getName()).append(" ").append(messageLengthHistogram.getDescription()).append("\n");
        output.append("# TYPE ").append(messageLengthHistogram.getName()).append(" histogram\n");
        output.append(messageLengthHistogram.toPrometheusFormat()).append("\n\n");

        // Response duration histogram
        output.append("# HELP ").append(responseDurationHistogram.getName()).append(" ").append(responseDurationHistogram.getDescription()).append("\n");
        output.append("# TYPE ").append(responseDurationHistogram.getName()).append(" histogram\n");
        output.append(responseDurationHistogram.toPrometheusFormat()).append("\n");

        return output.toString();
    }
}
