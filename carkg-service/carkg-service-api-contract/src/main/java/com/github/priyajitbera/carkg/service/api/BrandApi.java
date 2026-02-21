package com.github.priyajitbera.carkg.service.api;

import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.BrandEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.BrandEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("brand")
public interface BrandApi {

  @PostMapping
  ResponseEntity<BrandModel> save(@RequestBody BrandCreate create);

  @PostMapping("/batch")
  ResponseEntity<List<BrandModel>> saveBatch(@RequestBody List<BrandCreate> creates);

  @GetMapping("/{id}")
  ResponseEntity<BrandModel> get(@PathVariable("id") String id);

  @PostMapping("/embed")
  ResponseEntity<BrandEmbeddingModel> embedBatch(@RequestBody BrandEmbeddingRequest request);

  @PostMapping("/embed/all")
  ResponseEntity<List<BrandEmbeddingModel>> embedAll();

  @GetMapping("/semantic-search")
  ResponseEntity<List<BrandSemanticSearchModel>> semanticSearch(
      @RequestParam("query") String query);
}
