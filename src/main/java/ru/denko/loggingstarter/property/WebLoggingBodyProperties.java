package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.body")
public class WebLoggingBodyProperties {

    private Set<String> maskingFields = Collections.emptySet();
    private boolean enabled = true;
    private boolean isFull = true;

    public Set<String> getMaskingFields() {
        return maskingFields;
    }

    public void setMaskingFields(Set<String> maskingFields) {
        this.maskingFields = maskingFields;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}
