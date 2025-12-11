package frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import frontend.metrics.AppMetrics;

@Configuration
public class MetricsConfig {

    @Bean
    public AppMetrics appMetrics() {
        return new AppMetrics();
    }
}
