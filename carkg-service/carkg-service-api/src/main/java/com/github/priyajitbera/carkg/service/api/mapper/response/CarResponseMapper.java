package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.projection.CarSemanticSearchProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = CommonMapperConfig.class, uses = {BrandResponseMapper.class, EmbeddingResponseMapper.class})
public abstract class CarResponseMapper {

    @Mappings({
            @Mapping(target = "embedding", source = "embedding", qualifiedByName = "embeddingToFloatArray")
    })
    public abstract CarModel map(Car car);

    public abstract CarSemanticSearchModel map(CarSemanticSearchProjection car);
}
