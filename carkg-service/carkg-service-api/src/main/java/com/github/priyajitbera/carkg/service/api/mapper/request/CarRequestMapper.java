package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.*;
import com.github.priyajitbera.carkg.service.data.jpa.entity.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Mapper(config = CommonMapperConfig.class, uses = {
        ColorOptionRequestMapper.class, VariantRequestMapper.class, EngineRequestMapper.class, TransmissiongTypeRequestMapper.class})
public abstract class CarRequestMapper {

    @Autowired
    private ColorOptionRequestMapper colorOptionRequestMapper;
    @Autowired
    private VariantRequestMapper variantRequestMapper;
    @Autowired
    private EngineRequestMapper engineRequestMapper;
    @Autowired
    private TransmissiongTypeRequestMapper transmissiongTypeRequestMapper;

    @Mappings({
            @Mapping(target = "brand", source = ".", qualifiedBy = MapBrand.class),
            @Mapping(target = "colorOptions", qualifiedBy = MapColorOptions.class, dependsOn = {"brand", "name"}),
            @Mapping(target = "variants", qualifiedBy = MapVariants.class, dependsOn = {"brand", "name"}),
            @Mapping(target = "engines", qualifiedBy = MapEngines.class, dependsOn = {"brand", "name"}),
            @Mapping(target = "transmissionTypes", qualifiedBy = MapTransmissionTypes.class, dependsOn = {"brand", "name"})
    })
    public abstract void map(@MappingTarget Car car, CarCreate create, @Context CarRequestMappingContext context);


    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapBrand {
    }

    @MapBrand
    protected Brand mapBrand(CarCreate create, @Context CarRequestMappingContext context) {
        return context.brand();
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapColorOptions {
    }

    @MapColorOptions
    protected List<ColorOption> mapColorOptions(List<ColorOptionCreate> childItemsCreate, @Context CarRequestMappingContext context) {
        return ColorOptionRequestMapper.genericListItemMapper(colorOptionRequestMapper).map(context.car().getColorOptions(), childItemsCreate, context);
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapVariants {
    }

    @MapVariants
    protected List<Variant> mapVariants(List<VariantCreate> childItemsCreate, @Context CarRequestMappingContext context) {
        return VariantRequestMapper.genericListItemMapper(variantRequestMapper).map(context.car().getVariants(), childItemsCreate, context);
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapEngines {
    }

    @MapEngines
    protected List<Engine> mapEngines(List<EngineCreate> childItemsCreate, @Context CarRequestMappingContext context) {
        return EngineRequestMapper.genericListItemMapper(engineRequestMapper).map(context.car().getEngines(), childItemsCreate, context);
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapTransmissionTypes {
    }

    @MapTransmissionTypes
    protected List<TransmissionType> mapTransmissionTypes(List<TransmissionTypeCreate> childItemsCreate, @Context CarRequestMappingContext context) {
        return TransmissiongTypeRequestMapper.genericListItemMapper(transmissiongTypeRequestMapper).map(context.car().getTransmissionTypes(), childItemsCreate, context);
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Car car) {
        car.deriveAndSetId();
    }
}




