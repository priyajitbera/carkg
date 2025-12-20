package com.github.priyajitbera.carkg.service.data.rdf;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.springframework.stereotype.Component;

@Component
public class RdfMaterializer {

  public Model materialize(Model dataModel, Model ontologyModel) {
    // Create an RDFS reasoner
    Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();

    // Bind the reasoner to the ontology model
    reasoner = reasoner.bindSchema(ontologyModel);

    // Create an inference model
    InfModel infModel = ModelFactory.createInfModel(reasoner, dataModel);

    // The infModel contains both explicit and inferred triples.
    // To get a simple, materialized model, you can copy the contents.
    Model materializedModel = ModelFactory.createDefaultModel();
    materializedModel.add(infModel);

    return materializedModel;
  }
}
