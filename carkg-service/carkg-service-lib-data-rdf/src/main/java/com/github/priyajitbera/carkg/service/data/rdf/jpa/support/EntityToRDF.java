package com.github.priyajitbera.carkg.service.data.rdf.jpa.support;


import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.Entity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Component
public class EntityToRDF {

    private final Set<Class<?>> wrapperTypes = Set.of(Integer.class, Long.class, Float.class, Double.class, String.class);

    private final String NAMESPACE_ONTOLOGY;
    private final String NAMESPACE_DATA;

    public EntityToRDF(
            @Value("${namespace.ontology}")
            String namespaceOntology,
            @Value("${namespace.data}")
            String namespaceData
    ) {
        this.NAMESPACE_ONTOLOGY = namespaceOntology;
        this.NAMESPACE_DATA = namespaceData;
    }

    public <T extends Identifiable> Model entityToOntologyModel(Class<T> entityClass) {
        Model model = ModelFactory.createDefaultModel();
        entityToOntologyResource(model, entityClass);
        return model;
    }

    private Map.Entry<Resource, Property> entityToOntologyResource(Model model, Class<?> entityClass) {
        String uri = NAMESPACE_ONTOLOGY + entityClass.getSimpleName();
        Resource resource = model.createResource(uri);

        // example :Car a rdfs:Class .
        model.add(resource, RDF.type, RDFS.Class);

        Property resourceType = model.createProperty(NAMESPACE_ONTOLOGY + entityClass.getSimpleName());

        for (Field field : entityClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(RdfPredicate.class)) continue;
            Property property = model.createProperty(NAMESPACE_ONTOLOGY, field.getAnnotation(RdfPredicate.class).value());
            if (field.getType().isAnnotationPresent(Entity.class)) {
                // example :hasBrand a rdf:Property ;
                model.add(property, RDF.type, RDF.Property);
                property.addProperty(RDFS.domain, resourceType); // example: The subject of hasBrand must be a Car
                Map.Entry<Resource, Property> propertyResource = entityToOntologyResource(model, field.getType());
                property.addProperty(RDFS.range, propertyResource.getValue()); // example: The object of hasBrand must be a Brand
                property.addProperty(RDFS.label, field.getAnnotation(RdfPredicate.class).label());
                property.addProperty(RDFS.comment, field.getAnnotation(RdfPredicate.class).comment());
            } else {
                // example :hasBrand a rdf:Property ;
                model.add(property, RDF.type, RDF.Property);
                property.addProperty(RDFS.domain, resourceType); // example: The subject of hasBrand must be a Car
                property.addProperty(RDFS.label, field.getAnnotation(RdfPredicate.class).label());
                property.addProperty(RDFS.comment, field.getAnnotation(RdfPredicate.class).comment());
            }
        }

        return new Map.Entry<>() {
            @Override
            public Resource getKey() {
                return resource;
            }

            @Override
            public Property getValue() {
                return resourceType;
            }

            @Override
            public Property setValue(Property value) {
                return null;
            }
        };
    }

    public <T extends Identifiable> Model entityToDataModel(T entity) {
        Model model = ModelFactory.createDefaultModel();
        entityToDataModel(model, entity);
        return model;
    }

    private <T extends Identifiable> Map.Entry<Resource, Property> entityToDataModel(Model model, T entity) {
        String uri = NAMESPACE_DATA + entity.getId();
        Resource resource = model.createResource(uri);

        Property resourceType = model.createProperty(NAMESPACE_ONTOLOGY + entity.getClass().getSimpleName());

        // example :scorpio_n a :Car .
        resource.addProperty(RDF.type, resourceType);

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(RdfPredicate.class)) continue;
            field.setAccessible(true);
            Object fieldValue;
            try {
                fieldValue = field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            Property property = model.createProperty(NAMESPACE_ONTOLOGY, field.getAnnotation(RdfPredicate.class).value());
            if (fieldValue != null) {
                if (field.getType().isPrimitive() || wrapperTypes.contains(field.getType())) {
                    model.add(resource, property, fieldValue.toString());
                } else if (Identifiable.class.isAssignableFrom(field.getType())) {
                    Map.Entry<Resource, Property> propertyResource = entityToDataModel(model, (Identifiable) fieldValue);
                    resource.addProperty(property, propertyResource.getKey());
                }
            }
        }
        return Map.entry(resource, resourceType);
    }
}
