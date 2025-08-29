package ru.denko.loggingstarter.service;

import feign.Request;
import feign.Response;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.denko.loggingstarter.dto.RequestDirection;
import ru.denko.loggingstarter.property.WebLoggingFeignBodyProperties;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    private final WebLoggingFeignBodyProperties bodyProperties;

    public LoggingService(WebLoggingFeignBodyProperties bodyProperties) {
        this.bodyProperties = bodyProperties;
    }

    public void logFeignRequest(Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String headers = inlineHeaders(request.headers());

        String body = Strings.EMPTY;
        if (bodyProperties.isEnabled()) {
            body = new String(request.body(), StandardCharsets.UTF_8);
        }

        log.info("Запрос: {} {} {} {} body: {}", RequestDirection.OUT, method, requestURI, headers, body);
    }

    public void logFeignResponse(Response response, String responseBody) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        int status = response.status();

        if (!bodyProperties.isEnabled()) {
            responseBody = Strings.EMPTY;
        }

        log.info("Ответ: {} {} {} {} body: {}", RequestDirection.OUT, method, url, status, responseBody);
    }

    private String inlineHeaders(Map<String, Collection<String>> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = String.join(",", entry.getValue());

                    return headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(", "));

        return "headers={" + inlineHeaders + "}";
    }

}
