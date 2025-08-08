package ru.denko.loggingstarter.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;

public class MaskingUtils {

    private static final Logger log = LoggerFactory.getLogger(MaskingUtils.class);

    private MaskingUtils() {
        // private
    }

    public static String maskJsonFields(String responseBody, WebLoggingBodyProperties webLoggingBodyProperties) {
        try {
            DocumentContext ctx = JsonPath.parse(responseBody);
            for (String field : webLoggingBodyProperties.getMaskingFields()) {
                ctx.set(JsonPath.compile("$.." + field), webLoggingBodyProperties.getMask());
            }
            return ctx.jsonString();
        } catch (Exception e) {
            log.error("Failed to process JSON", e);
            return responseBody;
        }
    }

}
