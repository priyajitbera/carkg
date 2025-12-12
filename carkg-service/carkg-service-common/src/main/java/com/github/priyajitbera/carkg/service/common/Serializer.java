package com.github.priyajitbera.carkg.service.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public interface Serializer {

    static String toJsonString(Object object) {
        if (object == null)
            return "null";
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static String toJsonStringPretty(Object object) {
        if (object == null)
            return "null";
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static String toNatural(Object object) {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlFactory.disable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);

        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        mapper.registerModule(new JavaTimeModule());

        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
        mapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        try {
            return mapper
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static String toTokenOriented1(Object object) {
        if (object == null)
            return "null";
        try {
            return ToonConverter.convert(getObjectMapper().valueToTree(object));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes the object to a token-oriented format using Jackson YAML.
     * This serves as an alternative implementation using a popular library.
     */
    static String toTokenOriented(Object object) {
        if (object == null)
            return "null";
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
            yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);
            ObjectMapper mapper = new ObjectMapper(yamlFactory);
            mapper.registerModule(new JavaTimeModule());
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(object).trim();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    class ToonConverter {
        private static final String INDENT = "  ";

        static String convert(com.fasterxml.jackson.databind.JsonNode node) {
            StringBuilder sb = new StringBuilder();
            convert(node, 0, sb, true);
            return sb.toString().trim();
        }

        private static void convert(com.fasterxml.jackson.databind.JsonNode node, int level, StringBuilder sb,
                boolean isRoot) {
            if (node.isObject()) {
                if (!isRoot)
                    sb.append("\n");
                java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> fields = node
                        .fields();
                while (fields.hasNext()) {
                    java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> field = fields.next();
                    indent(sb, level);
                    sb.append(field.getKey()).append(": ");
                    com.fasterxml.jackson.databind.JsonNode value = field.getValue();
                    if (value.isObject() || (value.isArray() && !isPrimitiveArray(value))) {
                        convert(value, level + 1, sb, false);
                    } else {
                        convert(value, level, sb, false);
                    }
                    if (fields.hasNext() && !value.isObject() && !(value.isArray() && !isPrimitiveArray(value))) {
                        sb.append("\n");
                    }
                }
            } else if (node.isArray()) {
                if (node.isEmpty()) {
                    sb.append("[]");
                    return;
                }
                if (isPrimitiveArray(node)) {
                    sb.append("[");
                    for (int i = 0; i < node.size(); i++) {
                        if (i > 0)
                            sb.append(", ");
                        sb.append(node.get(i).asText());
                    }
                    sb.append("]");
                } else {
                    // Table format for objects
                    sb.append("\n");
                    java.util.Set<String> keys = new java.util.LinkedHashSet<>();
                    for (com.fasterxml.jackson.databind.JsonNode item : node) {
                        if (item.isObject()) {
                            java.util.Iterator<String> fieldNames = item.fieldNames();
                            while (fieldNames.hasNext())
                                keys.add(fieldNames.next());
                        }
                    }

                    if (keys.isEmpty())
                        return;

                    // Header
                    indent(sb, level);
                    sb.append("|");
                    for (String key : keys) {
                        sb.append(" ").append(key).append(" |");
                    }
                    sb.append("\n");

                    // Rows
                    for (com.fasterxml.jackson.databind.JsonNode item : node) {
                        indent(sb, level);
                        sb.append("|");
                        for (String key : keys) {
                            com.fasterxml.jackson.databind.JsonNode val = item.get(key);
                            String valStr = (val == null || val.isNull()) ? "" : val.asText();
                            sb.append(" ").append(valStr).append(" |");
                        }
                        sb.append("\n");
                    }
                    // Remove last newline
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                        sb.setLength(sb.length() - 1);
                    }
                }
            } else {
                sb.append(node.asText());
            }
        }

        private static boolean isPrimitiveArray(com.fasterxml.jackson.databind.JsonNode node) {
            if (!node.isArray())
                return false;
            for (com.fasterxml.jackson.databind.JsonNode item : node) {
                if (item.isObject() || item.isArray())
                    return false;
            }
            return true;
        }

        private static void indent(StringBuilder sb, int level) {
            for (int i = 0; i < level; i++) {
                sb.append(INDENT);
            }
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
