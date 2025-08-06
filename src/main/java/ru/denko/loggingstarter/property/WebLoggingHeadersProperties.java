package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.headers")
public class WebLoggingHeadersProperties {

    public enum Mode {
        INCLUDE,
        EXCLUDE,
        MASKING
    }

    private Mode mode = Mode.EXCLUDE;
    private Set<String> patterns = Collections.emptySet();
    private String mask = "******";

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

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
