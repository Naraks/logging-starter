package ru.denko.loggingstarter.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.AntPathMatcher;
import ru.denko.loggingstarter.property.WebLoggingEndpointsProperties;

import java.util.Optional;

public class LoggingUtils {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private LoggingUtils() {
        // private
    }

    public static boolean isUriLogging(WebLoggingEndpointsProperties webLoggingEndpointsProperties, String requestURI) {
        return webLoggingEndpointsProperties.getPatterns().stream()
                .noneMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    public static String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }

}
