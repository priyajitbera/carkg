package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.CombinationCreate;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.entity.*;
import org.apache.commons.lang3.function.TriConsumer;
import org.mapstruct.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

@Mapper(config = CommonMapperConfig.class)
public abstract class CombinationRequestMapper {

    @Mappings({
            @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class),
            @Mapping(target = "variant", qualifiedBy = MapVariant.class),
            @Mapping(target = "engine", qualifiedBy = MapEngine.class),
            @Mapping(target = "transmissionType", qualifiedBy = MapTransmissionType.class),
            @Mapping(target = "colorOption", qualifiedBy = MapColorOption.class)
    })
    public abstract void map(@MappingTarget Combination target, CombinationCreate src, @Context CarRequestMappingContext context);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapCar {
    }

    @MapCar
    protected Car mapCar(CombinationCreate src, @Context CarRequestMappingContext context) {
        return context.car();
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapVariant {
    }

    @MapVariant
    protected Variant mapVariant(String variantName, @Context CarRequestMappingContext context) {
        return context.car().getVariants().stream().filter(variant -> Objects.equals(variant.getName(), variantName)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Variant: %s not found for car: %s of brand: %s",
                        variantName, context.car().getName(), context.brand().getName())));
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapEngine {
    }

    @MapEngine
    protected Engine mapEngine(String engineName, @Context CarRequestMappingContext context) {
        return context.car().getEngines().stream().filter(engine -> Objects.equals(engine.getName(), engineName)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Engine: %s not found for car: %s of brand: %s",
                        engineName, context.car().getName(), context.brand().getName())));
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapTransmissionType {
    }

    @MapTransmissionType
    protected TransmissionType mapTransmissionType(String transmissionTypeName, @Context CarRequestMappingContext context) {
        return context.car().getTransmissionTypes().stream().filter(transmissionType -> Objects.equals(transmissionType.getName(), transmissionTypeName)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("TransmissionType: %s not found for car: %s of brand: %s",
                        transmissionTypeName, context.car().getName(), context.brand().getName())));
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapColorOption {
    }

    @MapColorOption
    protected ColorOption mapColorOption(String colorOptionName, @Context CarRequestMappingContext context) {
        return context.car().getColorOptions().stream().filter(colorOption -> Objects.equals(colorOption.getName(), colorOptionName)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ColorOption: %s not found for car: %s of brand: %s",
                        colorOptionName, context.car().getName(), context.brand().getName())));
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Combination target) {
        target.deriveAndSetId();
    }

    public static GenericListItemMapper<CombinationCreate, Combination, CarRequestMappingContext> genericListItemMapper(
            CombinationRequestMapper mapper
    ) {
        GenericListItemMapper<CombinationCreate,
                Combination, CarRequestMappingContext> listItemMapper = new GenericListItemMapper<>() {
            @Override
            TriConsumer<Combination, CombinationCreate, CarRequestMappingContext> getMapper() {
                return mapper::map;
            }

            @Override
            boolean match(Combination target, CombinationCreate src) {
                return Objects.equals(target.getId(), (new IdGen()).generate(target.getCar().deriveId(), src.getVariant(), src.getEngine(), src.getTransmissionType(), src.getColorOption()));
            }

            @Override
            Combination newTargetInstance() {
                return new Combination();
            }
        };
        return listItemMapper;
    }
}




