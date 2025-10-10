package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.mapper.request.BrandRequestMapper;
import com.github.priyajitbera.carkg.service.api.mapper.response.BrandResponseMapper;
import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.converter.FloatArrayToJson;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.repository.BrandRepository;
import org.springframework.ai.embedding.Embedding;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final EmbeddingService embeddingService;
    private final BrandRepository brandRepository;
    private final BrandRequestMapper brandRequestMapper;
    private final BrandResponseMapper brandResponseMapper;

    public BrandService(EmbeddingService embeddingService, BrandRepository brandRepository, BrandRequestMapper brandRequestMapper, BrandResponseMapper brandResponseMapper) {
        this.embeddingService = embeddingService;
        this.brandRepository = brandRepository;
        this.brandRequestMapper = brandRequestMapper;
        this.brandResponseMapper = brandResponseMapper;
    }

    @Transactional
    public BrandModel save(BrandCreate brandCreate) {

        Brand brandEntity = brandRepository.findByName(brandCreate.getName()).orElse(new Brand());
        brandRequestMapper.map(brandEntity, brandCreate);
        brandRepository.saveAndFlush(brandEntity);
        return brandResponseMapper.map(brandEntity);
    }

    public List<BrandSemanticSearchModel> semanticSearch(String query) {
        Embedding embedding = embeddingService.embedding(query);
        final String ser = new FloatArrayToJson().convertToDatabaseColumn(embedding.getOutput());
        return brandRepository.cosineSimilarity(ser)
                .stream().map(brandResponseMapper::map).toList();
    }

    @Transactional
    public List<BrandModel> saveBatch(List<BrandCreate> creates) {
        return creates.stream().map(this::save).collect(Collectors.toList());
    }

    public BrandModel findById(String id) {
        return brandRepository.findById(id).map(brandResponseMapper::map).orElseThrow();
    }
}
