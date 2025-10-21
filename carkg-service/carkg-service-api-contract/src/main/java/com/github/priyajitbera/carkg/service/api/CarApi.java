package com.github.priyajitbera.carkg.service.api;

import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.request.CarKgSyncRequest;
import com.github.priyajitbera.carkg.service.api.model.response.CarEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("car")
public interface CarApi {

    @PostMapping
    ResponseEntity<CarModel> save(@RequestBody CarCreate carCreate);

    @PostMapping("/batch")
    ResponseEntity<List<CarModel>> save(@RequestBody List<CarCreate> creates);

    @GetMapping("/{id}")
    ResponseEntity<CarModel> findById(@PathVariable("id") String id);

    @PostMapping("/embed")
    ResponseEntity<CarEmbeddingModel> embed(@RequestBody CarEmbeddingRequest embeddingRequest);

    @PostMapping("/embed/batch")
    ResponseEntity<List<CarEmbeddingModel>> embed(@RequestBody List<CarEmbeddingRequest> embeddingRequests);

    @GetMapping("/semantic-search")
    ResponseEntity<List<CarSemanticSearchModel>> semanticSearch(@RequestParam("query") String query);

    @PostMapping("/sync/kg")
    ResponseEntity<Void> syncKG(@RequestBody CarKgSyncRequest request);

    @PostMapping("/sync/kg/batch")
    ResponseEntity<Void> syncKG(@RequestBody List<CarKgSyncRequest> requests);
}
