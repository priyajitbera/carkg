package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.model.response.CarEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.projection.CarSemanticSearchProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = CommonMapperConfig.class,
    uses = {BrandResponseMapper.class})
public abstract class CarResponseMapper {

  public abstract CarModel map(Car car);

  public abstract CarSemanticSearchModel map(CarSemanticSearchProjection car);

  @Mappings(@Mapping(target = "embedding", source = "src.embedding.vector"))
  public abstract CarEmbeddingModel mapEmbeddingModel(Car src);
}
