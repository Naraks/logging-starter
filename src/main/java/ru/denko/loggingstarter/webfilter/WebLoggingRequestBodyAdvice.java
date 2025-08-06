package ru.denko.loggingstarter.webfilter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.util.MaskingUtils;

import java.lang.reflect.Type;

import static ru.denko.loggingstarter.util.LoggingUtils.formatQueryString;

public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);

    private final HttpServletRequest request;
    private final WebLoggingBodyProperties webLoggingBodyProperties;

    public WebLoggingRequestBodyAdvice(HttpServletRequest request, WebLoggingBodyProperties webLoggingBodyProperties) {
        this.request = request;
        this.webLoggingBodyProperties = webLoggingBodyProperties;
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

        String maskedBody = (String) body;
        if (webLoggingBodyProperties.isEnabled()) {
            maskedBody = MaskingUtils.maskJsonFields((String) body, webLoggingBodyProperties);
        }

        log.info("Тело запроса: {} {} {}", method, requestURI, maskedBody);

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

}
