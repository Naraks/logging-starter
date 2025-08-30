package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("logging.web-logging.feign-body")
public class WebLoggingFeignBodyProperties {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
