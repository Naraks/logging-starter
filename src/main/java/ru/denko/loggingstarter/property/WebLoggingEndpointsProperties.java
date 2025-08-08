package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.endpoints")
public class WebLoggingEndpointsProperties {

    private Set<String> patterns = Collections.emptySet();

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

}
