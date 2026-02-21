package com.github.priyajitbera.carkg.service.api;

import com.github.priyajitbera.carkg.service.api.model.request.AskFixedSchema;
import com.github.priyajitbera.carkg.service.api.model.response.RegisteredSparqlProjectionSchema;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reason")
public interface ReasoningApi {

  @PostMapping("generate-sparql")
  ResponseEntity<String> generateSparql(@RequestBody String question);

  @PostMapping("/ask-raw")
  ResponseEntity<String> askRaw(@RequestBody String question);

  @PostMapping("/ask-humanize")
  ResponseEntity<String> askHumanize(@RequestBody String question);

  @PostMapping("/ask-fixed-schema")
  ResponseEntity<Object> askFixedSchema(@RequestBody AskFixedSchema question);

  @GetMapping("/ask-fixed-schema/available-projections")
  ResponseEntity<List<RegisteredSparqlProjectionSchema>> getAvailableProjections();
}
