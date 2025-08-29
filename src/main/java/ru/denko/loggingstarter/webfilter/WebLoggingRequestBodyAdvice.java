package ru.denko.loggingstarter.webfilter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import ru.denko.loggingstarter.dto.RequestDirection;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.util.MaskingUtils;

import java.lang.reflect.Type;

import static ru.denko.loggingstarter.util.LoggingUtils.formatQueryString;
import static ru.denko.loggingstarter.util.LoggingUtils.isUriLogging;

public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);

    private final HttpServletRequest request;
    private final WebLoggingBodyProperties webLoggingBodyProperties;
    private final WebLoggingEndpointsProperties webLoggingEndpointsProperties;

    public WebLoggingRequestBodyAdvice(
            HttpServletRequest request,
            WebLoggingBodyProperties webLoggingBodyProperties,
            WebLoggingEndpointsProperties webLoggingEndpointsProperties
    ) {
        this.request = request;
        this.webLoggingBodyProperties = webLoggingBodyProperties;
        this.webLoggingEndpointsProperties = webLoggingEndpointsProperties;
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String maskedBody = MaskingUtils.maskJsonFields((String) body, webLoggingBodyProperties);

        log.info("Тело запроса: {} {} {} {}", RequestDirection.IN, method, requestURI, maskedBody);

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        boolean isUriLogging = isUriLogging(webLoggingEndpointsProperties, request.getRequestURI());

        return webLoggingBodyProperties.isEnabled() && isUriLogging;
    }

}
