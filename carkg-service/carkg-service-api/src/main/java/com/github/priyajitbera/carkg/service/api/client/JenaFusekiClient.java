package com.github.priyajitbera.carkg.service.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JenaFusekiClient {

  private final String JENA_FUSEKI_DATASET_URL;

  public JenaFusekiClient(@Value("${jenaFuseki.datasetUrl}") String fusekiDatasetUrl) {
    JENA_FUSEKI_DATASET_URL = fusekiDatasetUrl;
  }

  /** Run a SELECT query remotely on Fuseki */
  public List<String> runSelectQuery(String sparql) {
    List<String> results = new ArrayList<>();

    try (RDFConnection conn = RDFConnection.connect(JENA_FUSEKI_DATASET_URL)) {
      conn.querySelect(sparql, qs -> results.add(qs.toString()));
    }

    return results;
  }

  /**
   * Run a SELECT query remotely on Fuseki and return the results as a raw SPARQL Query Results JSON
   * string.
   */
  public String runSelectQueryJson(String sparql) {

    // Use ByteArrayOutputStream to capture the JSON output
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      // 1. Establish the connection
      try (RDFConnection conn = RDFConnection.connect(JENA_FUSEKI_DATASET_URL)) {

        // 2. Get the QueryExecution object
        // We use .query() to get the object for specialized execution
        QueryExecution qExec = conn.query(sparql);

        // 3. Execute the SELECT query
        ResultSet results = qExec.execSelect();

        // 4. Format the ResultSet into JSON and write it to the stream
        // This is the standard way to generate the SPARQL JSON output
        // from a ResultSet in Jena.
        ResultSetFormatter.outputAsJSON(outputStream, results);

        // IMPORTANT: Close the QueryExecution object
        qExec.close();

        // 5. Convert the captured bytes to a String (using UTF-8 encoding)
        return outputStream.toString(StandardCharsets.UTF_8);

      } catch (Exception e) {
        // Catch and handle exceptions (connection, query syntax, etc.)
        e.printStackTrace();
        throw new IOException("Error executing SPARQL query and retrieving JSON response.", e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Run an ASK query remotely */
  public boolean runAskQuery(String sparql) {
    try (RDFConnection conn = RDFConnection.connect(JENA_FUSEKI_DATASET_URL)) {
      return conn.queryAsk(sparql);
    }
  }

  /** Run an UPDATE remotely */
  public void runUpdate(String sparqlUpdate) {
    try (RDFConnection conn = RDFConnection.connect(JENA_FUSEKI_DATASET_URL)) {
      conn.update(sparqlUpdate);
    }
  }

  public void saveToFuseki(Model model) {
    try (RDFConnection conn = RDFConnection.connect(JENA_FUSEKI_DATASET_URL)) {
      conn.load(model); // Push model into Fuseki
    }
  }
}
