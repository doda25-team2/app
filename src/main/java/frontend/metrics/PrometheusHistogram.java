package frontend.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A histogram metric that tracks the distribution of observed values in buckets.
 * Histograms are used for measuring durations, sizes, etc.
 */
public class PrometheusHistogram {
    private final String name;
    private final String description;
    private final Map<String, String> labels;
    private final TreeMap<Double, AtomicLong> buckets;
    private final AtomicLong count;
    private final AtomicLong sum;

    public PrometheusHistogram(String name, String description, Map<String, String> labels, double[] bucketBoundaries) {
        this.name = name;
        this.description = description;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.buckets = new TreeMap<>();
        this.count = new AtomicLong(0);
        this.sum = new AtomicLong(0);

        // Initialize buckets
        for (double boundary : bucketBoundaries) {
            buckets.put(boundary, new AtomicLong(0));
        }
    }

    public synchronized void observe(double value) {
        count.incrementAndGet();
        sum.addAndGet((long) value);

        // Increment all buckets that are greater than or equal to the observed value
        for (Map.Entry<Double, AtomicLong> entry : buckets.entrySet()) {
            if (value <= entry.getKey()) {
                entry.getValue().incrementAndGet();
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String formatLabels(String additionalLabel) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        // Add base labels
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            first = false;
        }

        // Add additional label (like le="100")
        if (additionalLabel != null && !additionalLabel.isEmpty()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(additionalLabel);
        }

        sb.append("}");
        return sb.toString();
    }

    public String toPrometheusFormat() {
        StringBuilder sb = new StringBuilder();

        // Output bucket counts
        for (Map.Entry<Double, AtomicLong> entry : buckets.entrySet()) {
            sb.append(name).append("_bucket")
              .append(formatLabels("le=\"" + entry.getKey() + "\""))
              .append(" ").append(entry.getValue().get())
              .append("\n");
        }

        // Output +Inf bucket (always equals total count)
        sb.append(name).append("_bucket")
          .append(formatLabels("le=\"+Inf\""))
          .append(" ").append(count.get())
          .append("\n");

        // Output sum
        String baseLabels = labels.isEmpty() ? "" : formatLabels(null);
        sb.append(name).append("_sum")
          .append(baseLabels)
          .append(" ").append(sum.get())
          .append("\n");

        // Output count
        sb.append(name).append("_count")
          .append(baseLabels)
          .append(" ").append(count.get());

        return sb.toString();
    }
}
