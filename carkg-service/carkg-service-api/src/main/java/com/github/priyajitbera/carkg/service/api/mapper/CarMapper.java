package com.github.priyajitbera.carkg.service.api.mapper;

import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.projection.CarSemanticSearchProjection;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {
    private final BrandMapper brandMapper;
    private final EmbeddingMapper embeddingMapper;

    public CarMapper(BrandMapper brandMapper, EmbeddingMapper embeddingMapper) {
        this.brandMapper = brandMapper;
        this.embeddingMapper = embeddingMapper;
    }

    public CarModel to(Car car) {
        return CarModel.builder()
                .id(car.getId())
                .name(car.getName())
                .brand(brandMapper.to(car.getBrand()))
                .embedding(embeddingMapper.to(car.getEmbedding()))
                .build();

    }

    public CarSemanticSearchModel to(CarSemanticSearchProjection car) {
        return CarSemanticSearchModel.builder()
                .id(car.getId())
                .name(car.getName())
                .brandName(car.getBrandName())
                .brandCountryOfOrigin(car.getBrandCountryOfOrigin())
                .score(car.getScore())
                .build();

    }
}
