package ru.denko.loggingstarter.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.denko.loggingstarter.property.WebLoggingBodyProperties;

import java.io.IOException;

public class MaskingUtils {

    private static final Logger log = LoggerFactory.getLogger(MaskingUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MaskingUtils() {
        // private
    }

    public static String maskJsonFields(String responseBody, WebLoggingBodyProperties webLoggingBodyProperties) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.isObject()) {
                ObjectNode objectNode = (ObjectNode) root;
                maskJsonFields(objectNode, webLoggingBodyProperties);
                responseBody = objectMapper.writeValueAsString(root);
            }
        } catch (IOException ex) {
            log.error("Failed to parse body");
        }
        return responseBody;
    }

    public static void maskJsonFields(ObjectNode objectNode, WebLoggingBodyProperties webLoggingBodyProperties) {
        objectNode.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode value = entry.getValue();

            if (webLoggingBodyProperties.getFields().contains(fieldName)) {
                objectNode.put(fieldName, webLoggingBodyProperties.getMask());
            } else if (value.isObject()) {
                maskJsonFields((ObjectNode) value, webLoggingBodyProperties);
            } else if (value.isArray()) {
                value.forEach(element -> {
                    if (element.isObject()) {
                        maskJsonFields((ObjectNode) element, webLoggingBodyProperties);
                    }
                });
            }
        });
    }

}
