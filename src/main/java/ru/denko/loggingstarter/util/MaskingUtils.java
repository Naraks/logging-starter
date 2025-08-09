package ru.denko.loggingstarter.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;

public class MaskingUtils {

    private static final Logger log = LoggerFactory.getLogger(MaskingUtils.class);
    public static final String mask = "******";

    private MaskingUtils() {
        // private
    }

    public static String maskJsonFields(String responseBody, WebLoggingBodyProperties webLoggingBodyProperties) {
        try {
            DocumentContext ctx = JsonPath.parse(responseBody);
            for (String jsonPath : webLoggingBodyProperties.getMaskingFields()) {
                try {
                    Object value = ctx.read(jsonPath);

                    if (value instanceof String val && !webLoggingBodyProperties.isFull()) {
                        String maskedValue = partiallyMask(val);
                        ctx.set(JsonPath.compile(jsonPath), maskedValue);
                    }
                    else {
                        ctx.set(JsonPath.compile(jsonPath), mask);
                    }
                } catch (PathNotFoundException e) {
                    log.debug("Field not found with path: {}", jsonPath);
                }
            }
            return ctx.jsonString();
        } catch (Exception e) {
            log.error("Failed to process JSON", e);
            return responseBody;
        }
    }

    private static String partiallyMask(String value) {
        if (value == null || value.length() <= 5) {
            return mask;
        }

        int keepFirst = 3;
        int keepLast = 2;
        int maskedLength = value.length() - keepFirst - keepLast;

        String firstPart = value.substring(0, keepFirst);
        String lastPart = value.substring(value.length() - keepLast);

        return firstPart + "*".repeat(maskedLength) + lastPart;
    }

}
