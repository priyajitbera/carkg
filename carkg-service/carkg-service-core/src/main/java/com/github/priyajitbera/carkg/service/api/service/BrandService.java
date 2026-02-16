package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.embedding.BrandEmbeddableFormatter;
import com.github.priyajitbera.carkg.service.api.embedding.EmbeddingOperations;
import com.github.priyajitbera.carkg.service.api.exception.ResourceNotFoundException;
import com.github.priyajitbera.carkg.service.api.mapper.request.BrandRequestMapper;
import com.github.priyajitbera.carkg.service.api.mapper.response.BrandResponseMapper;
import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.BrandEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.BrandEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.converter.FloatArrayToJson;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.repository.BrandRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BrandService
    implements CreateReadOps<Brand, String, BrandCreate, BrandModel>,
        SemanticOps<BrandEmbeddingRequest, BrandEmbeddingModel, BrandSemanticSearchModel> {

  private final BrandEmbeddableFormatter brandEmbeddableFormatter;
  private final EmbeddingService embeddingService;
  private final BrandRepository brandRepository;
  private final BrandRequestMapper brandRequestMapper;
  private final BrandResponseMapper brandResponseMapper;

  public BrandService(
      BrandEmbeddableFormatter brandEmbeddableFormatter,
      EmbeddingService embeddingService,
      BrandRepository brandRepository,
      BrandRequestMapper brandRequestMapper,
      BrandResponseMapper brandResponseMapper) {
    this.brandEmbeddableFormatter = brandEmbeddableFormatter;
    this.embeddingService = embeddingService;
    this.brandRepository = brandRepository;
    this.brandRequestMapper = brandRequestMapper;
    this.brandResponseMapper = brandResponseMapper;
  }

  @Transactional
  public BrandModel save(BrandCreate create) {

    Brand brandEntity = brandRepository.findByName(create.getName()).orElse(new Brand());
    brandRequestMapper.map(brandEntity, create);
    brandRepository.saveAndFlush(brandEntity);
    return brandResponseMapper.map(brandEntity);
  }

  public List<BrandSemanticSearchModel> semanticSearch(String query) {
    float[] embedding = embeddingService.embed(query);
    final String ser = new FloatArrayToJson().convertToDatabaseColumn(embedding);
    return brandRepository.cosineSimilarity(ser).stream().map(brandResponseMapper::map).toList();
  }

  @Transactional
  public List<BrandModel> saveBatch(List<BrandCreate> creates) {
    return creates.stream().map(this::save).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public BrandModel findById(String id) {
    return brandRepository
        .findById(id)
        .map(brandResponseMapper::map)
        .orElseThrow(ResourceNotFoundException.brandById(id));
  }

  public BrandModel get(String id) {
    return brandRepository
        .findById(id)
        .map(brandResponseMapper::map)
        .orElseThrow(ResourceNotFoundException.brandById(id));
  }

  public BrandEmbeddingModel embed(BrandEmbeddingRequest request) {
    Brand brandEntity =
        brandRepository
            .findById(request.getId())
            .orElseThrow(ResourceNotFoundException.brandById(request.getId()));
    if (brandEntity.getEmbedding() == null) {
      brandEntity.setEmbedding(
          new com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding());
    }

    if (request.getForce()
        || (brandEntity.getEmbedding().getEmbeddingRefreshTillUtc() == null
            || brandEntity
                .getEmbedding()
                .getEmbeddingRefreshTillUtc()
                .isBefore(brandEntity.getUpdatedAtUtc()))) {

      float[] vector =
          EmbeddingOperations.generate(
              brandEntity, brandEmbeddableFormatter::format, embeddingService::embed);
      brandEntity.getEmbedding().setVector(vector);
      brandEntity.getEmbedding().setEmbeddingRefreshTillUtc(brandEntity.getUpdatedAtUtc());
      brandRepository.saveAndFlush(brandEntity);
    } else {
      log.info(
          "Skipping embedding for Brand with id: {} as it is already embedded at: {}",
          brandEntity.getId(),
          brandEntity.getEmbedding().getEmbeddingRefreshTillUtc());
    }

    return brandResponseMapper.mapEmbeddingModel(brandEntity);
  }

  public List<BrandEmbeddingModel> embedAll() {
    return brandRepository.findAll().stream()
        .map(brand -> BrandEmbeddingRequest.builder().id(brand.getId()).force(true).build())
        .map(this::embed)
        .toList();
  }
}
