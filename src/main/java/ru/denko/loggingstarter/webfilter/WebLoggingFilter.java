package ru.denko.loggingstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.denko.loggingstarter.dto.RequestDirection;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.property.WebLoggingHeadersProperties;
import ru.denko.loggingstarter.util.MaskingUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.denko.loggingstarter.util.LoggingUtils.formatQueryString;
import static ru.denko.loggingstarter.util.LoggingUtils.isUriLogging;

public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);

    private final WebLoggingHeadersProperties webLoggingHeadersProperties;
    private final WebLoggingEndpointsProperties webLoggingEndpointsProperties;
    private final WebLoggingBodyProperties webLoggingBodyProperties;

    public WebLoggingFilter(
            WebLoggingHeadersProperties webLoggingHeadersProperties,
            WebLoggingEndpointsProperties webLoggingEndpointsProperties,
            WebLoggingBodyProperties webLoggingBodyProperties
    ) {
        this.webLoggingHeadersProperties = webLoggingHeadersProperties;
        this.webLoggingEndpointsProperties = webLoggingEndpointsProperties;
        this.webLoggingBodyProperties = webLoggingBodyProperties;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        if (!isUriLogging(webLoggingEndpointsProperties, requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        String formattedRequestURI = requestURI + formatQueryString(request);
        String headers = inlineHeaders(request);

        log.info("Запрос: {} {} {} {}", RequestDirection.IN, method, formattedRequestURI, headers);

        try {
            super.doFilter(request, responseWrapper, chain);

            String responseHeaders = inlineHeaders(response);

            String formattedResponseBody = Strings.EMPTY;
            if (webLoggingBodyProperties.isEnabled()) {
                String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                String maskedResponseBody = MaskingUtils.maskJsonFields(responseBody, webLoggingBodyProperties);
                formattedResponseBody = "body=" + maskedResponseBody;
            }

            log.info("Ответ: {} {} {} {} {} {}", RequestDirection.IN, method, formattedRequestURI, response.getStatus(),
                    responseHeaders, formattedResponseBody);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private String inlineHeaders(HttpServletResponse response) {
        Map<String, String> headersMap = response.getHeaderNames().stream()
                .collect(Collectors.toMap(it -> it, it -> maskingHeader(response, it)));
        return inlineHeaders(headersMap);
    }

    private String inlineHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(it -> it, it -> maskingHeader(request, it)));
        return inlineHeaders(headersMap);
    }

    private String maskingHeader(HttpServletRequest request, String it) {
        if (shouldMask(it)) {
            return webLoggingHeadersProperties.getMask();
        }
        return request.getHeader(it);
    }

    private String maskingHeader(HttpServletResponse response, String it) {
        if (shouldMask(it)) {
            return webLoggingHeadersProperties.getMask();
        }
        return response.getHeader(it);
    }

    private boolean shouldMask(String it) {
        String lowerCaseHeader = it.toLowerCase();

        return webLoggingHeadersProperties.getMaskingHeaders()
                .stream()
                .map(String::toLowerCase)
                .anyMatch(lowerCaseHeader::equals);
    }

    private String inlineHeaders(Map<String, String> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = entry.getValue();

                    return headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(","));
        return "headers={" + inlineHeaders + "}";
    }

}
