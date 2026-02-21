package com.github.priyajitbera.carkg.service.data.rdf;

import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import com.github.priyajitbera.carkg.service.data.rdf.jpa.support.EntityToRDF;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.jena.rdf.model.Model;

public class OntologySchemaExtractor {

  public static final String[] KNOWN_FORMATS = {
    "JSON-LD", "N3", "N-TRIPLE", "RDF/XML", "RDF/XML-ABBREV", "TURTLE"
  };

  public void extract(
      String namespaceOntology,
      String namespaceData,
      String directory,
      Class<? extends Identifiable> rootType,
      Class<?> jsonView) {
    EntityToRDF entityToRDF = new EntityToRDF(namespaceOntology, namespaceData);
    Model ontologyModel = entityToRDF.entityToOntologyModel(rootType, jsonView);

    Arrays.stream(KNOWN_FORMATS)
        .forEach(
            format -> {
              String fileName = rdfFormatToConventionalOntologySchemaFileName(format);
              String filePath = directory + "/" + fileName;
              try {
                FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(
                    ("### Ontology in " + format + " format (TBox)\n\n")
                        .getBytes(StandardCharsets.UTF_8));
                ontologyModel.write(outputStream, format);
                outputStream.close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public static String rdfFormatToConventionalOntologySchemaFileName(String format) {
    return format.toLowerCase().replace("/", "_").replace("-", "_") + ".ttl";
  }
}
