package com.github.priyajitbera.carkg.service.api.mapper.request.context;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.FuelType;
import lombok.Builder;

import java.util.Set;

@Builder
public record CarRequestMappingContext(Brand brand, Car car, Set<FuelType> createdFuelTypes) {
}