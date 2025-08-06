package ru.denko.loggingstarter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.denko.loggingstarter.aspect.LogExecutionAspect;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.property.WebLoggingHeadersProperties;
import ru.denko.loggingstarter.webfilter.WebLoggingFilter;
import ru.denko.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

@AutoConfiguration
@EnableConfigurationProperties({
        WebLoggingHeadersProperties.class,
        WebLoggingEndpointsProperties.class,
        WebLoggingBodyProperties.class
})
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect() {
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter(
            WebLoggingHeadersProperties headersProperties,
            WebLoggingEndpointsProperties endpointsProperties,
            WebLoggingBodyProperties bodyProperties
    ) {
        return new WebLoggingFilter(headersProperties, endpointsProperties, bodyProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = { "enabled", "log-body" }, havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice(
            HttpServletRequest request,
            WebLoggingBodyProperties bodyProperties
    ) {
        return new WebLoggingRequestBodyAdvice(request, bodyProperties);
    }

}