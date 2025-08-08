package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.body")
public class WebLoggingBodyProperties {

    private Set<String> maskingFields = Collections.emptySet();
    private boolean enabled = true;
    private String mask = "******";

    public Set<String> getMaskingFields() {
        return maskingFields;
    }

    public void setMaskingFields(Set<String> maskingFields) {
        this.maskingFields = maskingFields;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
