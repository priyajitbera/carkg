package com.github.priyajitbera.carkg.service.data.rdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class OntologySchemaProvider {

    private final String ontologySchemaDir;

    private final Map<String, String> formatToOntology;


    public OntologySchemaProvider(
            @Value("${ontology-schema.dir:ontology}") String ontologySchemaDir
    ) {
        this.ontologySchemaDir = ontologySchemaDir;
        this.formatToOntology = new LinkedHashMap<>();

        Arrays.stream(OntologySchemaExtractor.KNOWN_FORMATS).forEach(format -> {
            String fileName = OntologySchemaExtractor.rdfFormatToConventionalOntologySchemaFileName(format);
            String filePath = ontologySchemaDir + "/" + fileName;
            Resource resource = new ClassPathResource(filePath);
            if (resource.exists()) {
                try {
                    this.formatToOntology.put(format, resource.getContentAsString(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Bean(name = "ontologySchemas")
    public Map<String, String> ontologySchemas() {
        return Map.copyOf(formatToOntology);
    }
}
