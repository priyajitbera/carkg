package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CommonMapperConfig.class)
public abstract class BrandRequestMapper {

    public abstract void map(@MappingTarget Brand brand, BrandCreate create);

    @AfterMapping
    protected void afterMapping(@MappingTarget Brand target) {
        target.derviceAndSetId();
    }
}


