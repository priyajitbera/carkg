package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.ReasoningApi;
import com.github.priyajitbera.carkg.service.api.model.request.AskFixedSchema;
import com.github.priyajitbera.carkg.service.api.model.response.RegisteredSparqlProjectionSchema;
import com.github.priyajitbera.carkg.service.api.service.ReasoningService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReasoningController implements ReasoningApi {

  private final ReasoningService reasoningService;

  public ReasoningController(ReasoningService reasoningService) {
    this.reasoningService = reasoningService;
  }

  @Override
  public ResponseEntity<String> generateSparql(@RequestBody String question) {
    String sparqlResponse = reasoningService.generateSparql(question);
    return ResponseEntity.ok(sparqlResponse);
  }

  @Override
  public ResponseEntity<String> askRaw(@RequestBody String question) {
    return ResponseEntity.ok(reasoningService.askRaw(question));
  }

  @Override
  public ResponseEntity<String> askHumanize(@RequestBody String question) {
    return ResponseEntity.ok(reasoningService.askHumanize(question));
  }

  @Override
  public ResponseEntity<Object> askFixedSchema(@RequestBody AskFixedSchema question) {
    return ResponseEntity.ok(reasoningService.askFixedSchema(question));
  }

  @Override
  public ResponseEntity<List<RegisteredSparqlProjectionSchema>> getAvailableProjections() {
    return ResponseEntity.ok(reasoningService.getAvailableProjections());
  }
}
