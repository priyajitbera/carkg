package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.CarApi;
import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.request.CarKgSyncRequest;
import com.github.priyajitbera.carkg.service.api.model.response.CarEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.service.CarService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController implements CarApi {

  private final CarService carService;

  public CarController(CarService carService) {
    this.carService = carService;
  }

  @Override
  public ResponseEntity<CarModel> save(@RequestBody CarCreate carCreate) {
    return ResponseEntity.ok(carService.save(carCreate));
  }

  @Override
  public ResponseEntity<List<CarModel>> save(List<CarCreate> creates) {
    return ResponseEntity.ok(carService.saveBatch(creates));
  }

  @Override
  public ResponseEntity<CarModel> findById(String id) {
    return ResponseEntity.ok(carService.findById(id));
  }

  @Override
  public ResponseEntity<CarEmbeddingModel> embed(CarEmbeddingRequest embeddingRequest) {
    return ResponseEntity.ok(carService.embed(embeddingRequest));
  }

  @Override
  public ResponseEntity<List<CarEmbeddingModel>> embed(
      List<CarEmbeddingRequest> embeddingRequests) {
    return ResponseEntity.ok(carService.embedBatch(embeddingRequests));
  }

  @Override
  public ResponseEntity<List<CarSemanticSearchModel>> semanticSearch(String query) {
    return ResponseEntity.ok(carService.semanticSearch(query));
  }

  @Override
  public ResponseEntity<Void> syncKG(CarKgSyncRequest request) {
    carService.syncKG(request);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> syncKG(List<CarKgSyncRequest> requests) {
    carService.syncKGBatch(requests);
    return ResponseEntity.ok().build();
  }
}
