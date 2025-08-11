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
    public static final int VISIBLE_PERCENTAGE = 20;

    private MaskingUtils() {
        // private
    }

    public static String maskJsonFields(String responseBody, WebLoggingBodyProperties webLoggingBodyProperties) {
        if (responseBody.isEmpty()) {
            return responseBody;
        }
        try {
            DocumentContext ctx = JsonPath.parse(responseBody);
            for (String jsonPath : webLoggingBodyProperties.getMaskingFields()) {
                try {
                    ctx.map(jsonPath, (value, configuration) -> {
                        if (value instanceof String strValue && !webLoggingBodyProperties.isFull()) {
                            return partiallyMask(strValue);
                        } else {
                            return mask;
                        }
                    });
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
        if (value == null) {
            return mask;
        }

        int totalLength = value.length();
        int visibleChars = (int) Math.round(totalLength * VISIBLE_PERCENTAGE / 100.0);

        int keepFirst = visibleChars / 2;
        int keepLast = visibleChars - keepFirst;

        keepFirst = Math.min(keepFirst, totalLength);
        keepLast = Math.min(keepLast, totalLength - keepFirst);

        String firstPart = value.substring(0, keepFirst);
        String lastPart = value.substring(value.length() - keepLast);

        return firstPart + "*".repeat(totalLength - keepFirst - keepLast) + lastPart;
    }

}
