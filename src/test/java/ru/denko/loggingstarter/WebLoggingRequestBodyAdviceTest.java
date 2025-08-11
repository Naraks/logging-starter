package ru.denko.loggingstarter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.property.WebLoggingHeadersProperties;
import ru.denko.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class WebLoggingRequestBodyAdviceTest {

    private WebLoggingRequestBodyAdvice advice;
    private ListAppender<ILoggingEvent> listAppender;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        WebLoggingHeadersProperties headersProps = new WebLoggingHeadersProperties();
        headersProps.setMaskingHeaders(Set.of("authorization", "cookie"));

        WebLoggingEndpointsProperties endpointsProps = new WebLoggingEndpointsProperties();
        endpointsProps.setPatterns(Set.of("/api/v2/link-infos"));

        WebLoggingBodyProperties bodyProps = new WebLoggingBodyProperties();
        bodyProps.setMaskingFields(Set.of("password", "token"));
        bodyProps.setFull(false);

        request = new MockHttpServletRequest();
        advice = new WebLoggingRequestBodyAdvice(request, bodyProps, endpointsProps);
    }

    @Test
    void shouldLogMaskBody() {
        request.setMethod("POST");
        request.setRequestURI("/api/user");
        request.setContentType("application/json");

        String requestBody = "{\"username\":\"user1\",\"token\":\"secret123\",\"email\":\"user@example.com\"}";
        request.setContent(requestBody.getBytes(StandardCharsets.UTF_8));

        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter parameter = mock(MethodParameter.class);

        advice.afterBodyRead(requestBody, inputMessage, parameter, null, null);

        String loggedResponse = listAppender.list.get(0).getFormattedMessage();

        assertThat(loggedResponse)
                .contains("\"token\":\"s*******3\"")
                .contains("\"email\":\"user@example.com\"")
                .doesNotContain("secret123");
    }

}
