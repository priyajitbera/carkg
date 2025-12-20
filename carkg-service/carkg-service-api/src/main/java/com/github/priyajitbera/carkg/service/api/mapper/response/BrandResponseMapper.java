package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.model.response.BrandEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.projection.BrandSemanticSearchProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = CommonMapperConfig.class)
public abstract class BrandResponseMapper {

  public abstract BrandModel map(Brand brand);

  public abstract BrandSemanticSearchModel map(BrandSemanticSearchProjection brand);

  @Mappings(@Mapping(target = "embedding", source = "src.embedding.vector"))
  public abstract BrandEmbeddingModel mapEmbeddingModel(Brand src);
}
