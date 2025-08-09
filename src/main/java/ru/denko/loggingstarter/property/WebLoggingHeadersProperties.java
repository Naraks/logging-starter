package ru.denko.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@ConfigurationProperties("logging.web-logging.headers")
public class WebLoggingHeadersProperties {

    private Set<String> maskingHeaders = Collections.emptySet();
    private String mask = "******";

    public Set<String> getMaskingHeaders() {
        return maskingHeaders;
    }

    public void setMaskingHeaders(Set<String> maskingHeaders) {
        this.maskingHeaders = maskingHeaders;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
