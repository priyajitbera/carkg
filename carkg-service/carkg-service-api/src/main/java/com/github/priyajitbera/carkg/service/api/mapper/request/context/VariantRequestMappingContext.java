package com.github.priyajitbera.carkg.service.api.mapper.request.context;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Variant;
import lombok.Builder;

@Builder
public record VariantRequestMappingContext(
    CarRequestMappingContext carRequestMappingContext, Variant variant) {}
