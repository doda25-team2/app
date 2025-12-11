package frontend.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A counter metric that only increases over time.
 * Counters are used for counting events like requests, errors, etc.
 */
public class PrometheusCounter {
    private final String name;
    private final String description;
    private final Map<String, String> labels;
    private final AtomicLong value;

    public PrometheusCounter(String name, String description, Map<String, String> labels) {
        this.name = name;
        this.description = description;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.value = new AtomicLong(0);
    }

    public void increment() {
        value.incrementAndGet();
    }

    public long getValue() {
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
