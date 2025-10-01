package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.mapper.BrandMapper;
import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.entity.SemanticObject;
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
    private final BrandMapper brandMapper;

    public BrandService(EmbeddingService embeddingService, BrandRepository brandRepository, BrandMapper brandMapper) {
        this.embeddingService = embeddingService;
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Transactional
    public BrandModel save(BrandCreate brandCreate) {

        String id = brandCreate.getName().replace(' ', '_').toLowerCase();
        Brand brandEntity = Brand.builder()
                .id(id)
                .name(brandCreate.getName())
                .countryOfOrigin(brandCreate.getCountryOfOrigin())
                .build();

        final String brandEntitySer = embeddingService.serializeEmbeddable(brandEntity);
        Embedding embedding = embeddingService.embedding(brandEntitySer);
        final String embeddingJson = embeddingService.serializeEmbedding(embedding);

        brandEntity.setSemanticObject(SemanticObject.builder().id(Brand.class.getName() + "-" + id).jsonView(brandEntitySer).build());
        brandEntity.setEmbedding(com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding.builder().embedding(embeddingJson).build());

        brandRepository.save(brandEntity);

        return brandMapper.to(brandEntity);
    }

    public List<BrandSemanticSearchModel> semanticSearch(String query) {
        Embedding embedding = embeddingService.embedding(query);
        final String ser = embeddingService.serializeEmbedding(embedding);
        return brandRepository.cosineSimilarity(ser)
                .stream().map(brandMapper::to).toList();
    }

    @Transactional
    public List<BrandModel> saveBatch(List<BrandCreate> creates) {
        return creates.stream().map(this::save).collect(Collectors.toList());
    }

    public BrandModel findById(String id) {
        return brandRepository.findById(id).map(brandMapper::to).orElseThrow();
    }
}
