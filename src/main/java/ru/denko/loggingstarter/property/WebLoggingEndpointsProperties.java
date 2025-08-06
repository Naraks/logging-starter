package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.endpoints")
public class WebLoggingEndpointsProperties {

    public enum Mode {
        INCLUDE,
        EXCLUDE
    }

    private Mode mode = Mode.EXCLUDE;
    private Set<String> patterns = Collections.emptySet();

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }
}
