package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.BrandApi;
import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.BrandEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.BrandEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.service.BrandService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandController implements BrandApi {

  private final BrandService brandService;

  public BrandController(BrandService brandService) {
    this.brandService = brandService;
  }

  @Override
  public ResponseEntity<BrandModel> save(BrandCreate create) {
    return ResponseEntity.ok(brandService.save(create));
  }

  @Override
  public ResponseEntity<List<BrandModel>> saveBatch(List<BrandCreate> creates) {
    return ResponseEntity.ok(brandService.saveBatch(creates));
  }

  @Override
  public ResponseEntity<BrandModel> get(String id) {
    return ResponseEntity.ok(brandService.get(id));
  }

  @Override
  public ResponseEntity<BrandEmbeddingModel> embedBatch(BrandEmbeddingRequest request) {
    return ResponseEntity.ok(brandService.embed(request));
  }

  @Override
  public ResponseEntity<List<BrandEmbeddingModel>> embedAll() {
    return ResponseEntity.ok(brandService.embedAll());
  }

  @Override
  public ResponseEntity<List<BrandSemanticSearchModel>> semanticSearch(String query) {
    return ResponseEntity.ok(brandService.semanticSearch(query));
  }
}
