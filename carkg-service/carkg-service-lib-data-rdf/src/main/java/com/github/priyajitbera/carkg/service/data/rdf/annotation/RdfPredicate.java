package com.github.priyajitbera.carkg.service.data.rdf.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map a Java field to a semantic RDF property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RdfPredicate {
    String value();
    String label();
    String comment();
}