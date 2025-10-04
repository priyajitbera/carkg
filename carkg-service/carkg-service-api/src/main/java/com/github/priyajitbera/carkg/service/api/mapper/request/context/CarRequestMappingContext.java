package com.github.priyajitbera.carkg.service.api.mapper.request.context;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import lombok.Builder;

@Builder
public record CarRequestMappingContext(Brand brand, Car car) {
}
