package frontend.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A gauge metric that can go up or down.
 * Gauges are used for values that can increase or decrease like current queue size, temperature, etc.
 */
public class PrometheusGauge {
    private final String name;
    private final String description;
    private final Map<String, String> labels;
    private final AtomicInteger value;

    public PrometheusGauge(String name, String description, Map<String, String> labels) {
        this.name = name;
        this.description = description;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.value = new AtomicInteger(0);
    }

    public void set(int newValue) {
        value.set(newValue);
    }

    public int getValue() {
        return value.get();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String formatLabels() {
        if (labels.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public String toPrometheusFormat() {
        return name + formatLabels() + " " + value.get();
    }
}
