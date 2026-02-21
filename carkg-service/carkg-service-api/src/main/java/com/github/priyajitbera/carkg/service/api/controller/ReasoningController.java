package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.model.request.AskFixedSchema;
import com.github.priyajitbera.carkg.service.api.model.response.RegisteredSparqlProjectionSchema;
import com.github.priyajitbera.carkg.service.api.service.ReasoningService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reason")
public class ReasoningController {

  private final ReasoningService reasoningService;

  public ReasoningController(ReasoningService reasoningService) {
    this.reasoningService = reasoningService;
  }

  @PostMapping("generate-sparql")
  public ResponseEntity<String> generateSparql(@RequestBody String question) {
    String sparqlResponse = reasoningService.generateSparql(question);
    return ResponseEntity.ok(sparqlResponse);
  }

  @PostMapping("/ask-raw")
  public ResponseEntity<String> askRaw(@RequestBody String question) {
    return ResponseEntity.ok(reasoningService.askRaw(question));
  }

  @PostMapping("/ask-humanize")
  public ResponseEntity<String> askHumanize(@RequestBody String question) {
    return ResponseEntity.ok(reasoningService.askHumanize(question));
  }

  @PostMapping("/ask-fixed-schema")
  public ResponseEntity<Object> askFixedSchema(@RequestBody AskFixedSchema question) {
    return ResponseEntity.ok(reasoningService.askFixedSchema(question));
  }

  @GetMapping("/ask-fixed-schema/available-projections")
  public ResponseEntity<List<RegisteredSparqlProjectionSchema>> getAvailableProjections() {
    return ResponseEntity.ok(reasoningService.getAvailableProjections());
  }
}
