package com.github.priyajitbera.carkg.service.jena.config;

import com.github.priyajitbera.carkg.service.jena.annoations.SparqlProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class ProjectionRegistrar implements BeanFactoryPostProcessor {

    public static class ProjectionsContainer {
        private final Map<Class<?>, String> projectionSchemaTypeToExtractedColumns = new LinkedHashMap<>();

        protected void add(final Class<?> projectionSchemaType, final String extractedProjectionColumns) {
            projectionSchemaTypeToExtractedColumns.put(projectionSchemaType, extractedProjectionColumns);
        }

        public Optional<Map.Entry<Class<?>, String>> get(final String schemaTypeName) {
            final Optional<Map.Entry<Class<?>, String>> matchedEntryOpt = projectionSchemaTypeToExtractedColumns.entrySet().stream()
                    .filter(entry -> entry.getKey().getName().equals(schemaTypeName)).findFirst();
            return matchedEntryOpt.map(matchedEntry -> Map.entry(matchedEntry.getKey(), matchedEntry.getValue()));
        }

        public List<Map.Entry<Class<?>, String>> getAll() {
            final List<Map.Entry<Class<?>, String>> all = projectionSchemaTypeToExtractedColumns.entrySet().stream()
                    .map(entry -> Map.<Class<?>, String>entry(entry.getKey(), entry.getValue())).toList();
            return all;
        }
    }

    public abstract String getSparqlProjectionsBasePackage();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SparqlProjectionExtractor projectionExtractor = new SparqlProjectionExtractor();

        ProjectionConfiguration config = beanFactory.getBean(ProjectionConfiguration.class);

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resourcePatternResolver);

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(getSparqlProjectionsBasePackage()) + "/**/*.class";

        AnnotationTypeFilter annotationFilter = new AnnotationTypeFilter(SparqlProjection.class);

        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                    if (annotationFilter.match(metadataReader, metadataReaderFactory)) {
                        String className = metadataReader.getClassMetadata().getClassName();
                        // Note: Class.forName() can throw ClassNotFoundException
                        Class<?> projectionSchemaType = Class.forName(className);
                        config.registerSparqlSelectProjection(projectionSchemaType, projectionExtractor.extractProjection(projectionSchemaType));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during projection scanning: " + e.getMessage());
        }
    }
}
