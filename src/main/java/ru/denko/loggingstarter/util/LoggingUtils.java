package ru.denko.loggingstarter.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

public class LoggingUtils {

    private LoggingUtils() {
        // private
    }

    public static String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }

}
