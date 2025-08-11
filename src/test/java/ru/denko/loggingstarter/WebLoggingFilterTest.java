package ru.denko.loggingstarter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;
import ru.denko.loggingstarter.property.WebLoggingHeadersProperties;
import ru.denko.loggingstarter.webfilter.WebLoggingFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WebLoggingFilterTest {

    private WebLoggingFilter filter;
    private ListAppender<ILoggingEvent> listAppender;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(WebLoggingFilter.class);
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

        filter = new WebLoggingFilter(headersProps, endpointsProps, bodyProps);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    void shouldLogRequestWithHeaders() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer token123");
        request.addHeader("Content-Type", "application/json");

        filter.doFilter(request, response, filterChain);

        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs.get(0).getFormattedMessage())
                .contains("Запрос: GET /api/users")
                .contains("Authorization=******")
                .contains("Content-Type=application/json");
    }

    @Test
    void shouldNotLogEndpoints() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/v2/link-infos");
        request.addHeader("Authorization", "Bearer token123");
        request.addHeader("Content-Type", "application/json");

        filter.doFilter(request, response, filterChain);

        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.isEmpty());
    }

    @Test
    void shouldLogMaskBody() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/user");
        request.setContentType("application/json");

        String responseBody = "{\"username\":\"user1\",\"token\":\"secret123\",\"email\":\"user@example.com\"}";

        filter.doFilter(request, response, new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException {
                response.getWriter().write(responseBody);
            }
        });

        String loggedResponse = listAppender.list.get(1).getFormattedMessage();

        assertThat(loggedResponse)
                .contains("\"token\":\"s*******3\"")
                .contains("\"email\":\"user@example.com\"")
                .doesNotContain("secret123");
    }

}
