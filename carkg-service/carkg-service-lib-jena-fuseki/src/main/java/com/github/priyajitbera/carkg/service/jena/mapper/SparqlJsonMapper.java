package com.github.priyajitbera.carkg.service.jena.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SparqlJsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Maps a SPARQL Query Results JSON string to a List of the specified POJO type.
     * The POJO field names must match the SPARQL query variable names (case-insensitive).
     *
     * @param <T>        The target POJO type.
     * @param sparqlJson The raw JSON response from Fuseki.
     * @param targetType The Class object of the target POJO (e.g., Car.class).
     * @return A List of the mapped POJO objects.
     */
    public <T> List<T> map(String sparqlJson, Class<T> targetType) {

        List<T> results = new ArrayList<>();

        // 1. Parse the entire JSON string into a tree structure
        JsonNode rootNode;
        try {
            rootNode = MAPPER.readTree(sparqlJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. Locate the "bindings" array, which contains the actual results
        JsonNode bindingsNode = rootNode
                .path("results")
                .path("bindings");

        // 3. Find the field names in the target POJO and map them to lowercase for matching
        // This acts as a cache for reflection.
        Field[] targetFields = targetType.getDeclaredFields();

        // 4. Iterate over each binding (each row/solution)
        for (JsonNode binding : bindingsNode) {
            // Create a new instance of the target POJO
            T pojoInstance;
            try {
                pojoInstance = targetType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            // 5. Iterate over the fields of the target POJO
            for (Field field : targetFields) {
                // SPARQL variables start with '?', but Java fields don't.
                // We use the field name (e.g., "name") to look up the variable.
                String varName = field.getName();

                // 6. Locate the variable node in the current binding
                // Example: binding.path("carName")
                JsonNode varNode = binding.path(varName);

                // 7. Extract the actual value from the SPARQL JSON structure
                if (varNode.isMissingNode()) {
                    // Skip if the binding doesn't contain this variable (optional/projected out)
                    continue;
                }

                // Navigate to the "value" field (e.g., binding.carName.value)
                String value = varNode.path("value").asText();

                // 8. Use Reflection to set the field value dynamically
                // Make the field accessible (important for private fields)
                field.setAccessible(true);

                // Assuming all SPARQL literals map to String fields in the POJO
                try {
                    field.set(pojoInstance, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            results.add(pojoInstance);
        }

        return results;
    }
}
