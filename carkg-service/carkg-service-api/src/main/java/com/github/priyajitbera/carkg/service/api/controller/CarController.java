package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.CarEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.service.CarService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("car")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public CarModel save(@RequestBody CarCreate carCreate) {
        return carService.save(carCreate);
    }

    @PostMapping("/batch")
    public List<CarModel> save(@RequestBody List<CarCreate> creates) {
        return carService.saveBatch(creates);
    }

    @GetMapping("/{id}")
    public CarModel findById(@PathVariable("id") String id) {
        return carService.findById(id);
    }

    @PostMapping("/embed")
    public CarEmbeddingModel embed(@RequestBody CarEmbeddingRequest embeddingRequest) {
        return carService.embed(embeddingRequest);
    }

    @GetMapping("/semantic-search")
    public List<CarSemanticSearchModel> semanticSearch(@Param("query") String query) {
        return carService.semanticSearch(query);
    }
}
