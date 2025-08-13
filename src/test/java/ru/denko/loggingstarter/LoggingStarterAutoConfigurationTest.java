package ru.denko.loggingstarter;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import ru.denko.loggingstarter.aspect.LogExecutionAspect;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.property.WebLoggingHeadersProperties;
import ru.denko.loggingstarter.webfilter.WebLoggingFilter;
import ru.denko.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LoggingStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LoggingStarterAutoConfiguration.class));

    @Test
    void shouldConfigureWebLoggingRequestBodyAdviceWhenPropertySet() {
        this.contextRunner
                .withPropertyValues("logging.web-logging.enabled=true")
                .withPropertyValues("logging.web-logging.log-body=true")
                .withBean(HttpServletRequest.class, () -> mock(HttpServletRequest.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(WebLoggingRequestBodyAdvice.class);
                });
    }

    @Test
    void shouldNotConfigureWebLoggingRequestBodyAdviceWhenPropertyDisabled() {
        this.contextRunner
                .withPropertyValues("logging.web-logging.enabled=false")
                .withPropertyValues("logging.web-logging.log-body=false")
                .withBean(HttpServletRequest.class, () -> mock(HttpServletRequest.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(WebLoggingRequestBodyAdvice.class);
                });
    }

    @Test
    void shouldConfigureWebLoggingFilterWhenPropertySet() {
        this.contextRunner
                .withPropertyValues("logging.web-logging.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(WebLoggingFilter.class);
                });
    }

    @Test
    void shouldNotConfigureWebLoggingFilterWhenPropertyDisabled() {
        this.contextRunner
                .withPropertyValues("logging.web-logging.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(WebLoggingFilter.class);
                });
    }

    @Test
    void shouldConfigureLogExecutionAspectWhenPropertySet() {
        this.contextRunner
                .withPropertyValues("logging.log-exec-time=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(LogExecutionAspect.class);
                });
    }

    @Test
    void shouldNotConfigureLogExecutionAspectWhenPropertyDisabled() {
        this.contextRunner
                .withPropertyValues("logging.log-exec-time=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(LogExecutionAspect.class);
                });
    }

    @Test
    void shouldBindBodyConfigurationProperties() {
        Set<String> expected = new HashSet<>();
        expected.add("$..password");
        this.contextRunner
                .withPropertyValues("logging.web-logging.body.maskingFields=$..password")
                .run(context -> {
                    WebLoggingBodyProperties properties = context.getBean(WebLoggingBodyProperties.class);
                    assertThat(properties.getMaskingFields()).isEqualTo(expected);
                });
    }

    @Test
    void shouldBindEndpointsConfigurationProperties() {
        Set<String> expected = new HashSet<>();
        expected.add("/api/v2/link-infos");
        this.contextRunner
                .withPropertyValues("logging.web-logging.endpoints.patterns=/api/v2/link-infos")
                .run(context -> {
                    WebLoggingEndpointsProperties properties = context.getBean(WebLoggingEndpointsProperties.class);
                    assertThat(properties.getPatterns()).isEqualTo(expected);
                });
    }

    @Test
    void shouldBindHeadersConfigurationProperties() {
        Set<String> expected = new HashSet<>();
        expected.add("Content-Type");
        this.contextRunner
                .withPropertyValues("logging.web-logging.headers.maskingHeaders=Content-Type")
                .run(context -> {
                    WebLoggingHeadersProperties properties = context.getBean(WebLoggingHeadersProperties.class);
                    assertThat(properties.getMaskingHeaders()).isEqualTo(expected);
                });
    }
}
