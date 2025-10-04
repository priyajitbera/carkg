package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.projection.BrandSemanticSearchProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = CommonMapperConfig.class, uses = EmbeddingResponseMapper.class)
public abstract class BrandResponseMapper {

    @Mappings({
            @Mapping(target = "embedding", source = "embedding", qualifiedByName = "embeddingToFloatArray")
    })
    public abstract BrandModel map(Brand brand);

    public abstract BrandSemanticSearchModel map(BrandSemanticSearchProjection brand);
}
