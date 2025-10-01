package com.github.priyajitbera.carkg.service.api.mapper;

import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.projection.BrandSemanticSearchProjection;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    private final EmbeddingMapper embeddingMapper;

    public BrandMapper(EmbeddingMapper embeddingMapper) {
        this.embeddingMapper = embeddingMapper;
    }

    public BrandModel to(Brand brand) {

        return BrandModel.builder()
                .id(brand.getId())
                .name(brand.getName())
                .countryOfOrigin(brand.getCountryOfOrigin())
                .embedding(embeddingMapper.to(brand.getEmbedding()))
                .build();
    }

    public BrandSemanticSearchModel to(BrandSemanticSearchProjection brand) {

        return BrandSemanticSearchModel.builder()
                .id(brand.getId())
                .name(brand.getName())
                .countryOfOrigin(brand.getCountryOfOrigin())
                .score(brand.getScore())
                .build();
    }
}
