package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.projection.CarSemanticSearchProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {BrandMapper.class, EmbeddingMapper.class})
public abstract class CarMapper {

    @Mappings({
            @Mapping(target = "brand", source = "brand"),
            @Mapping(target = "embedding", source = "embedding", qualifiedByName = "embeddingToFloatArray")
    })
    public abstract CarModel map(Car car);

    public abstract CarSemanticSearchModel map(CarSemanticSearchProjection car);
}
