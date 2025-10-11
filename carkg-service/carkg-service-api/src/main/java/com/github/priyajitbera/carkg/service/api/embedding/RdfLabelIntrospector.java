package com.github.priyajitbera.carkg.service.api.embedding;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RdfLabelIntrospector extends JacksonAnnotationIntrospector {

    private final Map<Annotated, PropertyName> cacheFindNameForSerialization = new ConcurrentHashMap<>();
    private final Map<Annotated, PropertyName> cacheFindNameForDeserialization = new ConcurrentHashMap<>();

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        return cacheFindNameForSerialization.computeIfAbsent(a, key -> {
            RdfPredicate rdfPredicate = a.getAnnotation(RdfPredicate.class);
            if (rdfPredicate != null && !rdfPredicate.label().isEmpty()) {
                return new PropertyName(rdfPredicate.label());
            }
            return super.findNameForSerialization(a);
        });
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        return cacheFindNameForDeserialization.computeIfAbsent(a, key -> {
            RdfPredicate rdfPredicate = a.getAnnotation(RdfPredicate.class);
            if (rdfPredicate != null && !rdfPredicate.label().isEmpty()) {
                return new PropertyName(rdfPredicate.label());
            }
            return super.findNameForDeserialization(a);
        });
    }
}