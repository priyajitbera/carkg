package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.BrandEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.BrandEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.service.BrandService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public BrandModel save(@RequestBody BrandCreate create) {
        return brandService.save(create);
    }

    @PostMapping("/batch")
    public List<BrandModel> saveBatch(@RequestBody List<BrandCreate> creates) {
        return brandService.saveBatch(creates);
    }

    @GetMapping("/{id}")
    public BrandModel get(@PathVariable("id") String id) {
        return brandService.get(id);
    }

    @PostMapping("/embed")
    public BrandEmbeddingModel embed(@RequestBody BrandEmbeddingRequest request) {
        return brandService.embed(request);
    }

    @GetMapping("/semantic-search")
    public List<BrandSemanticSearchModel> semanticSearch(@Param("query") String query) {
        return brandService.semanticSearch(query);
    }
}
