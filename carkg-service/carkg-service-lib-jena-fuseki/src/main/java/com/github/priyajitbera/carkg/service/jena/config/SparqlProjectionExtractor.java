package com.github.priyajitbera.carkg.service.jena.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SparqlProjectionExtractor {
  /**
   * Inspects a Java POJO class and generates a string of SPARQL SELECT projection variables (e.g.,
   * "?carName ?brandName") based on the POJO's field names.
   *
   * @param pojoClass The Class object of the target POJO (e.g., Car.class).
   * @return A string containing space-separated SPARQL variables.
   */
  public String extractProjection(Class<?> pojoClass) {
    // Use Java Reflection to get all declared fields (public, private, protected)
    Field[] fields = pojoClass.getDeclaredFields();

    // Stream the fields, map each field name to a SPARQL variable, and join them
    String projectionString =
        Arrays.stream(fields)
            .map(Field::getName) // Get the field name (e.g., "carName")
            .map(name -> "?" + name) // Prepend '?' (e.g., "?carName")
            .collect(Collectors.joining(" ")); // Join with a space

    return projectionString;
  }

  /**
   * Optional: Generates a human-readable list of required fields for AI model context. * @param
   * pojoClass The Class object of the target POJO.
   *
   * @return A comma-separated string of field names (e.g., "carName, brandName").
   */
  private String extractFieldNames(Class<?> pojoClass) {
    return Arrays.stream(pojoClass.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.joining(", "));
  }
}
